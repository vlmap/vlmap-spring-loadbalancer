package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ServerWebExchangeBodyUtil {

    private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "__cached_request_body_object__";

    private static DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    public static Mono<ServerWebExchange> set(ServerWebExchange exchange, byte[] bytes) {
        DataBuffer buffer = dataBufferFactory.wrap(bytes);
        return set(exchange, buffer);
    }

    public static Mono<ServerWebExchange> set(ServerWebExchange exchange, DataBuffer buffer) {
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, buffer);
        return cache(exchange);
    }

    /**
     * 缓存body
     *
     * @param exchange
     * @return
     */
//    public static Mono<ServerWebExchange> cache(ServerWebExchange exchange) {
//
//        DataBuffer buffer = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
//        if (buffer == null) {
//            return DataBufferUtils.join(exchange.getRequest().getBody())
//                    .flatMap(dataBuffer -> {
//                        dataBuffer = DataBufferUtils.retain(dataBuffer);
//                        return parser(exchange, dataBuffer);
//                    });
//        } else {
//            buffer = DataBufferUtils.retain(buffer);
//            return parser(exchange, buffer);
//
//
//        }
//
//    }
    public static Mono<ServerWebExchange> cache(ServerWebExchange exchange) {

        DataBuffer buffer = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
        if (buffer == null) {
            return Mono.from(exchange.getRequest().getBody().flatMap(dataBuffer -> {
                        dataBuffer = DataBufferUtils.retain(dataBuffer);
                        return parser(exchange, dataBuffer);
            }));
        } else {
            buffer = DataBufferUtils.retain(buffer);
            return parser(exchange, buffer);


        }

    }
    private static Mono<ServerWebExchange> parser(ServerWebExchange exchange, DataBuffer buffer) {

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
//        ServerWebExchangeDecorator d= new ServerWebExchangeDecorator(exchange);

        ServerWebExchange instance = exchange.mutate().request(mutatedRequest).build();
        return Mono.just(instance);
    }
//    class DelegateServerWebExchangeDecorator  extends  ServerWebExchangeDecorator{
//
//
//        public  DelegateServerWebExchangeDecorator(ServerWebExchange delegate) {
//            super(delegate);
//
//        }
//        		this.formDataMono = initFormData(request, codecConfigurer, getLogPrefix());
//
//    }

}
