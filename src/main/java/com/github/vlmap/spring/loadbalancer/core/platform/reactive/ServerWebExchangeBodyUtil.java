package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ServerWebExchangeBodyUtil {
    private static final ResolvableType FORM_DATA_TYPE =
            ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);


    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA =
            Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, String>(0)))
                    .cache();
    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "__cached_request_body_object__";

    private static DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    public static Mono<ServerWebExchange> set(ServerWebExchange exchange, byte[] bytes, ServerCodecConfigurer codecConfigurer) {
        DataBuffer buffer = dataBufferFactory.wrap(bytes);
        return set(exchange, buffer, codecConfigurer);
    }

    public static Mono<ServerWebExchange> set(ServerWebExchange exchange, DataBuffer buffer, ServerCodecConfigurer codecConfigurer) {
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, buffer);
        return cache(exchange, codecConfigurer);
    }


    public static Mono<ServerWebExchange> cache(ServerWebExchange exchange, ServerCodecConfigurer codecConfigurer) {


        DataBuffer buffer = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
        if (buffer == null) {
            return body(exchange, codecConfigurer);
        } else {
            buffer = DataBufferUtils.retain(buffer);
            return parser(exchange, buffer, codecConfigurer);


        }

    }


    private static Mono<ServerWebExchange> body(ServerWebExchange exchange, ServerCodecConfigurer codecConfigurer) {

        return Mono.from(exchange.getRequest().getBody().flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer);

            return parser(exchange, dataBuffer, codecConfigurer);
        }));
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

    private static Mono<ServerWebExchange> parser(ServerWebExchange exchange, DataBuffer buffer, ServerCodecConfigurer codecConfigurer) {

        int length = buffer.readableByteCount();

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.setContentLength(length);
        HttpHeaders resultHeaders = HttpHeaders.readOnlyHttpHeaders(headers);

        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {

            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(buffer.slice(0, length));
            }

            @Override
            public HttpHeaders getHeaders() {
                return resultHeaders;
            }


        };

        Mono<MultiValueMap<String, String>> formDataMono = codecConfigurer == null ? null : initFormData(mutatedRequest, codecConfigurer, exchange.getLogPrefix());


        ServerWebExchange instance = new DelegateServerWebExchangeDecorator(exchange.mutate().request(mutatedRequest).build(), formDataMono);
        return Mono.just(instance);
    }

    static class DelegateServerWebExchangeDecorator extends ServerWebExchangeDecorator {
        private Mono<MultiValueMap<String, String>> formDataMono;

        public DelegateServerWebExchangeDecorator(ServerWebExchange delegate, Mono<MultiValueMap<String, String>> formDataMono) {
            super(delegate);
            this.formDataMono = formDataMono;

        }

        @Override
        public Mono<MultiValueMap<String, String>> getFormData() {
            return this.formDataMono == null ? super.getFormData() : this.formDataMono;
        }
    }

}
