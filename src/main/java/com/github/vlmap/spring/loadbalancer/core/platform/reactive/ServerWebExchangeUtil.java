package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyPredicateFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public class ServerWebExchangeUtil {
    private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "__cached_request_body_object__";

    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies
            .withDefaults().messageReaders();
    private static    Class<? extends byte[]> inClass = new byte[0].getClass();
    private static  DataBufferFactory dataBufferFactory=new DefaultDataBufferFactory();

    public static Mono<ServerWebExchange> body(ServerWebExchange  exchange,byte[] bytes){
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY,bytes);
        return body(exchange);
    }
    public static Mono<ServerWebExchange> body(ServerWebExchange  exchange) {


            byte[] cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);

            // We can only read the body from the request once, once that happens if we
            // try to read the body again an exception will be thrown. The below if/else
            // caches the body object as a request attribute in the ServerWebExchange
            // so if this filter is run more than once (due to more than one route
            // using it) we do not try to read the request body multiple times
            if (cachedBody == null) {
                // Join all the DataBuffers so we have a single DataBuffer for the body
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            // Update the retain counts so we can read the body twice,
                            // once to parse into an object
                            // that we can test the predicate against and a second time
                            // when the HTTP client sends
                            // the request downstream
                            // Note: if we end up reading the body twice we will run into
                            // a problem, but as of right
                            // now there is no good use case for doing this
                            DataBufferUtils.retain(dataBuffer);
                            // Make a slice for each read so each read has its own
                            // read/write indexes
                            Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(
                                    dataBuffer.slice(0, dataBuffer.readableByteCount())));

                            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                    exchange.getRequest()) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    return Flux.just( dataBufferFactory.wrap(cachedBody));
                                }
                            };
                            ServerWebExchange instance=exchange.mutate().request(mutatedRequest)
                                    .build();
                             return ServerRequest
                                    .create(instance, messageReaders)
                                    .bodyToMono(inClass).doOnNext(objectValue -> {
                                        exchange.getAttributes().put(
                                                CACHE_REQUEST_BODY_OBJECT_KEY,
                                                objectValue);

                                    }).thenReturn(instance);
                        });
            }else{

                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                        exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return Flux.just( dataBufferFactory.wrap(cachedBody));
                    }
                };
                ServerWebExchange instance=exchange.mutate().request(mutatedRequest)
                        .build();
                return ServerRequest
                        .create(instance, messageReaders)
                        .bodyToMono(inClass).doOnNext(objectValue -> {
                            exchange.getAttributes().put(
                                    CACHE_REQUEST_BODY_OBJECT_KEY,
                                    objectValue);

                        }).thenReturn(instance);
            }


    }
}
