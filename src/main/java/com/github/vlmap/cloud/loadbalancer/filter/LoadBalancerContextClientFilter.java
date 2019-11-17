package com.github.vlmap.cloud.loadbalancer.filter;


import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



public class LoadBalancerContextClientFilter extends org.springframework.cloud.gateway.filter.LoadBalancerClientFilter {

    public LoadBalancerContextClientFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties) {
        super(loadBalancer, properties);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> mono = null;
        try {
            TagContextHolder.set(TagContextHolder.REQUEST, exchange.getRequest());
            TagContextHolder.set(TagContextHolder.RESPONSE, exchange.getResponse());
            mono = super.filter(exchange, chain);
        } finally {

            TagContextHolder.dispose();
        }
        return mono;
    }

//
}
