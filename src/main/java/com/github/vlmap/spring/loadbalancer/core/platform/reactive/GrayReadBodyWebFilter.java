package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

public class GrayReadBodyWebFilter extends ReadBodyFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ResolvableType FORM_DATA_TYPE =
            ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);

    private static final ResolvableType MULTIPART_DATA_TYPE = ResolvableType.forClassWithGenerics(
            MultiValueMap.class, String.class, Part.class);

    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA =
            Mono.just(org.springframework.util.CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, String>(0)))
                    .cache();

    private static final Mono<MultiValueMap<String, Part>> EMPTY_MULTIPART_DATA =
            Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, Part>(0)))
                    .cache();


    @Autowired(required = false)
    HttpHandler httpHandler;
    ServerCodecConfigurer codecConfigurer = ServerCodecConfigurer.create();

    HttpWebHandlerAdapter httpWebHandlerAdapter = null;

    public GrayReadBodyWebFilter(GrayLoadBalancerProperties properties) {
        super(properties);
    }


    @PostConstruct
    public void initMethod() {
        if (httpHandler instanceof HttpWebHandlerAdapter) {
            httpWebHandlerAdapter = (HttpWebHandlerAdapter) httpHandler;
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        HttpMethod method = exchange.getRequest().getMethod();
        if (use(contentType, method)) {
            exchange.getAttributes().put(READ_BODY_TAG, true);

            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        ServerHttpRequest request = new ServerHttpRequestDecorator(exchange.getRequest()) {

                            @Override
                            public Flux<DataBuffer> getBody() {
                                DataBufferUtils.retain(dataBuffer);
                                return Flux.defer(() -> Flux.just(
                                        dataBuffer.slice(0, dataBuffer.readableByteCount())));
                            }


                        };

                        Mono<MultiValueMap<String, String>> formDataMono = initFormData(request, getCodecConfigurer(), exchange.getLogPrefix());

                        Mono<MultiValueMap<String, Part>> multipartDataMono = initMultipartData(request, getCodecConfigurer(), exchange.getLogPrefix());

                        return chain.filter(new DelegateServerWebExchangeDecorator(exchange.mutate().request(request).build(), formDataMono, multipartDataMono));

                    });

        }

        return chain.filter(exchange);

    }


    protected ServerCodecConfigurer getCodecConfigurer() {
        return httpWebHandlerAdapter == null ? codecConfigurer : httpWebHandlerAdapter.getCodecConfigurer();
    }


    @SuppressWarnings("unchecked")
    private static Mono<MultiValueMap<String, String>> initFormData(ServerHttpRequest request,
                                                                    ServerCodecConfigurer configurer, String logPrefix) {

        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return ((HttpMessageReader<MultiValueMap<String, String>>) configurer.getReaders().stream()
                        .filter(reader -> reader.canRead(FORM_DATA_TYPE, MediaType.APPLICATION_FORM_URLENCODED))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No form data HttpMessageReader.")))
                        .readMono(FORM_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix))
                        .switchIfEmpty(EMPTY_FORM_DATA)
                        .cache();
            }
        } catch (InvalidMediaTypeException ex) {
            // Ignore
        }
        return EMPTY_FORM_DATA;
    }

    @SuppressWarnings("unchecked")
    private static Mono<MultiValueMap<String, Part>> initMultipartData(ServerHttpRequest request,
                                                                       ServerCodecConfigurer configurer, String logPrefix) {

        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
                return ((HttpMessageReader<MultiValueMap<String, Part>>) configurer.getReaders().stream()
                        .filter(reader -> reader.canRead(MULTIPART_DATA_TYPE, MediaType.MULTIPART_FORM_DATA))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No multipart HttpMessageReader.")))
                        .readMono(MULTIPART_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix))
                        .switchIfEmpty(EMPTY_MULTIPART_DATA)
                        .cache();
            }
        } catch (InvalidMediaTypeException ex) {
            // Ignore
        }
        return EMPTY_MULTIPART_DATA;
    }

    public int getOrder() {
        return FilterOrder.ORDER_READ_BODY_FILTER;
    }

    static class DelegateServerWebExchangeDecorator extends ServerWebExchangeDecorator {
        private Mono<MultiValueMap<String, String>> formDataMono;
        private Mono<MultiValueMap<String, Part>> multipartDataMono;

        public DelegateServerWebExchangeDecorator(ServerWebExchange delegate, Mono<MultiValueMap<String, String>> formDataMono, Mono<MultiValueMap<String, Part>> multipartDataMono) {
            super(delegate);
            this.formDataMono = formDataMono;
            this.multipartDataMono = multipartDataMono;
        }

        @Override
        public Mono<MultiValueMap<String, String>> getFormData() {
            return this.formDataMono == null ? super.getFormData() : this.formDataMono;
        }

        @Override
        public Mono<MultiValueMap<String, Part>> getMultipartData() {
            return this.multipartDataMono == null ? super.getMultipartData() : this.multipartDataMono;
        }
    }

}
