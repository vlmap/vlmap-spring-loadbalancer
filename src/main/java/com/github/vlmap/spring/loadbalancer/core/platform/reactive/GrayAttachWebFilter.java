package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.AttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ReactiveAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class GrayAttachWebFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    private ReactiveAttachHandler attachHandler;


    public GrayAttachWebFilter(GrayLoadBalancerProperties properties, ReactiveAttachHandler attachHandler) {

        this.properties = properties;
        this.attachHandler = attachHandler;

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!this.properties.getAttach().isEnabled()) {
            return chain.filter(exchange);
        }
        List<GaryAttachParamater> paramaters = attachHandler.getAttachParamaters();
        AttachHandler.SimpleRequestData data = new AttachHandler.SimpleRequestData();
        if (CollectionUtils.isNotEmpty(paramaters)) {
            List<String> headers = new ArrayList<>();

            return initData(exchange, data)
                    .doOnNext((o) -> attachHandler.match(data, paramaters, headers))
                    .flatMap(object -> {
                        if (CollectionUtils.isNotEmpty(headers)) {
                            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                            for (String header : headers) {
                                builder.header(properties.getHeaderName(), header);
                            }

                            return chain.filter(exchange.mutate().request(builder.build()).build());
                        }
                        return chain.filter(exchange);
                    });
        }
        return chain.filter(exchange);


    }

    /**
     * 收集参数
     *
     * @return
     */
    protected Mono<AttachHandler.SimpleRequestData> initData(ServerWebExchange exchange, AttachHandler.SimpleRequestData data) {
        return attachHandler.parser(data, exchange);
    }


    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}
