package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import com.github.vlmap.spring.loadbalancer.util.RequestUtils;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;


public class ReadBodyWebFilter extends ReadBodyFilter implements WebFilter {


    @Autowired(required = false)
    HttpHandler httpHandler;

    HttpWebHandlerAdapter httpWebHandlerAdapter = null;

    public ReadBodyWebFilter(GrayLoadBalancerProperties properties) {
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
        if (Util.isEnabled(this.properties.getCacheBody())) {
            MediaType contentType = exchange.getRequest().getHeaders().getContentType();

            HttpMethod method = exchange.getRequest().getMethod();
            if (RequestUtils.useBody(properties, contentType, method, exchange.getRequest().getHeaders().getContentLength())) {
                exchange.getAttributes().put(READ_BODY_TAG, true);
                if (logger.isTraceEnabled()) {
                    logger.trace(" apply cachebody , requestId:" + exchange.getRequest().getId());
                }
                return ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
                    ServerWebExchange decorator = new DelegateServerWebExchangeDecorator(exchange.mutate().request(serverHttpRequest).build(), getCodecConfigurer());


                    return chain.filter(decorator)
                            .doFinally(s -> {
                                PooledDataBuffer dataBuffer = (PooledDataBuffer) decorator.getAttributes()
                                        .remove(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
                                if (dataBuffer != null && dataBuffer.isAllocated()) {

                                    dataBuffer.release();
                                }
                            });


                });
            }
        }
        return chain.filter(exchange);

    }


    protected ServerCodecConfigurer getCodecConfigurer() {
        return httpWebHandlerAdapter == null ? null : httpWebHandlerAdapter.getCodecConfigurer();
    }


}
