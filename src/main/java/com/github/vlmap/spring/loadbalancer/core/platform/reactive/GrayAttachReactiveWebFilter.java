package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.AttachHandler;
import com.github.vlmap.spring.loadbalancer.core.cli.GaryAttachParamater;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

public class GrayAttachReactiveWebFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    AttachHandler attachHandler;

    public GrayAttachReactiveWebFilter(GrayLoadBalancerProperties properties, AttachHandler attachHandler) {

        this.properties = properties;
        this.attachHandler = attachHandler;

    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        List<GaryAttachParamater> paramaters = attachHandler.getAttachParamaters();
        if (CollectionUtils.isNotEmpty(paramaters)) {

          return  Mono.from( AttachHandler.parser(paramaters, new AttachHandler.SimpleRequestData(), exchange)).flatMap(data -> {
                ServerHttpRequest.Builder builder=  exchange.getRequest().mutate();
                for (GaryAttachParamater paramater : paramaters) {
                    String value = attachHandler.attach(paramater, data);
                    if (StringUtils.isNotBlank(value)) {
                        builder.header(properties.getHeaderName(), value);

                    }
                }
                ServerWebExchange object= exchange.mutate().request( builder.build()).build() ;
                return chain.filter(object);
            });


        } else {
            return chain.filter(exchange);

        }
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
