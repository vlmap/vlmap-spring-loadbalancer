package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;


public class ServerWebExchangeBodyUtil {

    private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "__cached_request_body_object__";

    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies
            .withDefaults().messageReaders();
    private static DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    public static Mono<ServerWebExchange> set(ServerWebExchange exchange, byte[] bytes) {
        DataBuffer buffer = dataBufferFactory.wrap(bytes);
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, buffer);
        return cache(exchange);
    }

    /**
     * 缓存body
     *
     * @param exchange
     * @return
     */
    public static Mono<ServerWebExchange> cache(ServerWebExchange exchange) {

        byte[] buffer = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
        if (buffer == null) {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = null;
                        InputStream inputStream=dataBuffer.asInputStream();
                        try {
                            bytes = IOUtils.toByteArray(inputStream);
                            exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, bytes);
                        } catch (Exception e) {

                        }finally {
                            IOUtils.closeQuietly(inputStream);
                            DataBufferUtils.release(dataBuffer);
                        }


                        return make(exchange, bytes);
                    });
        } else {
            return make(exchange, buffer);


        }

    }

    private static Mono<ServerWebExchange> make(ServerWebExchange exchange, byte[] bytes) {


        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(dataBufferFactory.wrap(bytes));
            }
        };
        ServerWebExchange instance = exchange.mutate().request(mutatedRequest).build();
        return Mono.just(instance);
    }

//    public static Mono<ServerWebExchange> body(ServerWebExchange  exchange) {
//
//
//        DataBuffer cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
//
//        // We can only read the body from the request once, once that happens if we
//        // try to read the body again an exception will be thrown. The below if/else
//        // caches the body object as a request attribute in the ServerWebExchange
//        // so if this filter is run more than once (due to more than one route
//        // using it) we do not try to read the request body multiple times
//        if (cachedBody == null) {
//
//            // Join all the DataBuffers so we have a single DataBuffer for the body
//            return DataBufferUtils.join(exchange.getRequest().getBody())
//                    .flatMap(dataBuffer -> {
//                        // Update the retain counts so we can read the body twice,
//                        // once to parse into an object
//                        // that we can test the predicate against and a second time
//                        // when the HTTP client sends
//                        // the request downstream
//                        // Note: if we end up reading the body twice we will run into
//                        // a problem, but as of right
//                        // now there is no good use case for doing this
//                        DataBufferUtils.retain(dataBuffer);
//                        // Make a slice for each read so each read has its own
//                        // read/write indexes
//
//                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
//                                exchange.getRequest()) {
//                            @Override
//                            public Flux<DataBuffer> getBody() {
//                                return Flux.just(
//                                        dataBuffer.slice(0, dataBuffer.readableByteCount()));
//                            }
//                        };
//                        ServerWebExchange instance=exchange.mutate().request(mutatedRequest)
//                                .build();
//                        return Mono.just(instance);
//                    });
//        }else{
//
//            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
//                    exchange.getRequest()) {
//                @Override
//                public Flux<DataBuffer> getBody() {
//                    return Flux.just( dataBufferFactory.wrap(cachedBody));
//                }
//            };
//            ServerWebExchange instance=exchange.mutate().request(mutatedRequest)
//                    .build();
//
//            return ServerRequest
//                    .create(instance, messageReaders)
//                    .bodyToMono(inClass).doOnNext(objectValue -> {
//                        exchange.getAttributes().put(
//                                CACHE_REQUEST_BODY_OBJECT_KEY,
//                                objectValue);
//
//                    }).thenReturn(instance);
//        }
//
//
//    }
}
