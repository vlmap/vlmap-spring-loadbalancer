package org.springframework.web.reactive.result.method;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GrayInvocableHandlerMethod extends InvocableHandlerMethod {
    private static final Mono<Object[]> EMPTY_ARGS = Mono.just(new Object[0]);
    private ReactiveAdapterRegistry reactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
    private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
    private GrayLoadBalancerProperties properties;

    public void setProperties(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    private static final Object NO_ARG_VALUE = new Object();

    public GrayInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public Mono<HandlerResult> invoke(
            ServerWebExchange exchange, BindingContext bindingContext, Object... providedArgs) {

        return getMethodArgumentValues(exchange, bindingContext, providedArgs).flatMap(args -> {
            Object value;
            try {
                ReflectionUtils.makeAccessible(getBridgedMethod());
                if (properties != null) {
                    String headerName = properties.getHeaderName();

                    String tag = exchange.getRequest().getHeaders().getFirst(headerName);
                    ContextManager.getRuntimeContext().put(RuntimeContext.REQUEST_TAG_REFERENCE,tag);
                    ContextManager.getRuntimeContext().put(RuntimeContext.REACTIVE_SERVER_WEB_EXCHANGE, exchange);


                }

                value = getBridgedMethod().invoke(getBean(), args);
            } catch (IllegalArgumentException ex) {
                assertTargetBean(getBridgedMethod(), getBean(), args);
                String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
                return Mono.error(new IllegalStateException(formatInvokeError(text, args), ex));
            } catch (InvocationTargetException ex) {
                return Mono.error(ex.getTargetException());
            } catch (Throwable ex) {
                // Unlikely to ever get here, but it must be handled...
                return Mono.error(new IllegalStateException(formatInvokeError("Invocation failure", args), ex));
            } finally {

                if (properties != null) {
                    ContextManager.getRuntimeContext().onComplete();
                }
            }


            HttpStatus status = getResponseStatus();
            if (status != null) {
                exchange.getResponse().setStatusCode(status);
            }

            MethodParameter returnType = getReturnType();
            ReactiveAdapter adapter = this.reactiveAdapterRegistry.getAdapter(returnType.getParameterType());
            boolean asyncVoid = isAsyncVoidReturnType(returnType, adapter);
            if ((value == null || asyncVoid) && isResponseHandled(args, exchange)) {
                return (asyncVoid ? Mono.from(adapter.toPublisher(value)) : Mono.empty());
            }

            HandlerResult result = new HandlerResult(this, value, returnType, bindingContext);
            return Mono.just(result);
        });
    }

    private Mono<Object[]> getMethodArgumentValues(
            ServerWebExchange exchange, BindingContext bindingContext, Object... providedArgs) {

        if (ObjectUtils.isEmpty(getMethodParameters())) {
            return EMPTY_ARGS;
        }
        MethodParameter[] parameters = getMethodParameters();
        List<Mono<Object>> argMonos = new ArrayList<>(parameters.length);
        for (MethodParameter parameter : parameters) {
            parameter.initParameterNameDiscovery(this.getParameterNameDiscoverer());
            Object providedArg = findProvidedArgument(parameter, providedArgs);
            if (providedArg != null) {
                argMonos.add(Mono.just(providedArg));
                continue;
            }
            if (!this.resolvers.supportsParameter(parameter)) {
                return Mono.error(new IllegalStateException(
                        formatArgumentError(parameter, "No suitable resolver")));
            }
            try {
                argMonos.add(this.resolvers.resolveArgument(parameter, bindingContext, exchange)
                        .defaultIfEmpty(NO_ARG_VALUE)
                        .doOnError(cause -> logArgumentErrorIfNecessary(exchange, parameter, cause)));
            } catch (Exception ex) {
                logArgumentErrorIfNecessary(exchange, parameter, ex);
                argMonos.add(Mono.error(ex));
            }
        }
        return Mono.zip(argMonos, values ->
                Stream.of(values).map(o -> o != NO_ARG_VALUE ? o : null).toArray());
    }

    private static boolean isAsyncVoidReturnType(MethodParameter returnType, @Nullable ReactiveAdapter adapter) {
        if (adapter != null && adapter.supportsEmpty()) {
            if (adapter.isNoValue()) {
                return true;
            }
            Type parameterType = returnType.getGenericParameterType();
            if (parameterType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) parameterType;
                if (type.getActualTypeArguments().length == 1) {
                    return Void.class.equals(type.getActualTypeArguments()[0]);
                }
            }
        }
        return false;
    }

    private boolean isResponseHandled(Object[] args, ServerWebExchange exchange) {
        if (getResponseStatus() != null || exchange.isNotModified()) {
            return true;
        }
        for (Object arg : args) {
            if (arg instanceof ServerHttpResponse || arg instanceof ServerWebExchange) {
                return true;
            }
        }
        return false;
    }

    private void logArgumentErrorIfNecessary(
            ServerWebExchange exchange, MethodParameter parameter, Throwable cause) {

        // Leave stack trace for later, if error is not handled..
        String message = cause.getMessage();
        if (!message.contains(parameter.getExecutable().toGenericString())) {
            if (logger.isDebugEnabled()) {
                logger.debug(exchange.getLogPrefix() + formatArgumentError(parameter, message));
            }
        }
    }

    /**
     * Configure the argument resolvers to use to use for resolving method
     * argument values against a {@code SERVER_WEB_EXCHANGE}.
     */
    public void setArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        super.setArgumentResolvers(resolvers);
        this.resolvers.addResolvers(resolvers);
    }

    public void setReactiveAdapterRegistry(ReactiveAdapterRegistry registry) {
        super.setReactiveAdapterRegistry(registry);
        this.reactiveAdapterRegistry = registry;
    }
}
