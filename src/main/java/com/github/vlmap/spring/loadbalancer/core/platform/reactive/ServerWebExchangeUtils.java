package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

class ServerWebExchangeUtils {
    private static final Log log = LogFactory.getLog(ServerWebExchangeUtils.class);

    public static final String CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR = "cachedServerHttpRequestDecorator";

    public static final String CACHED_REQUEST_BODY_ATTR = "cachedRequestBody";

    public static <T> Mono<T> cacheRequestBody(ServerWebExchange exchange,
                                               Function<ServerHttpRequest, Mono<T>> function) {
        return cacheRequestBody(exchange, false, function);
    }

    /**
     * Caches the request body in a ServerWebExchange attribute. The attribute is
     * {@link #CACHED_REQUEST_BODY_ATTR}. If this method is called from a location that
     * can not mutate the ServerWebExchange (such as a Predicate), setting
     * cacheDecoratedRequest to true will put a {@link ServerHttpRequestDecorator} in an
     * attribute {@link #CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR} for adaptation later.
     *
     * @param exchange              the available ServerWebExchange.
     * @param cacheDecoratedRequest if true, the ServerHttpRequestDecorator will be
     *                              cached.
     * @param function              a function that accepts the created ServerHttpRequestDecorator.
     * @param <T>                   generic type for the return {@link Mono}.
     * @return Mono of type T created by the function parameter.
     */
    private static <T> Mono<T> cacheRequestBody(ServerWebExchange exchange,
                                                boolean cacheDecoratedRequest,
                                                Function<ServerHttpRequest, Mono<T>> function) {
        // Join all the DataBuffers so we have a single DataBuffer for the body
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    if (dataBuffer.readableByteCount() > 0) {
                        if (log.isTraceEnabled()) {
                            log.trace("retaining body in exchange attribute");
                        }
                        exchange.getAttributes().put(CACHED_REQUEST_BODY_ATTR,
                                dataBuffer);
                    }

                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Mono.<DataBuffer>fromSupplier(() -> {
                                if (exchange.getAttributeOrDefault(
                                        CACHED_REQUEST_BODY_ATTR, null) == null) {
                                    // probably == downstream closed
                                    return null;
                                }
                                // TODO: deal with Netty
                                NettyDataBuffer pdb = (NettyDataBuffer) dataBuffer;
                                return pdb.factory()
                                        .wrap(pdb.getNativeBuffer().retainedSlice());
                            }).flux();
                        }
                    };
                    if (cacheDecoratedRequest) {
                        exchange.getAttributes().put(
                                CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR, decorator);
                    }
                    return function.apply(decorator);
                });
    }
}
