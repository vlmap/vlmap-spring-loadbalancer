package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;


import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class TagLoadBalancerClientFilter extends org.springframework.cloud.gateway.filter.LoadBalancerClientFilter {
     private ReactorTagProcess process;

    public TagLoadBalancerClientFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties,ReactorTagProcess process) {
        super(loadBalancer, properties);
        this.process=process;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> mono = null;
        try {
            GatewayContextHolder.set(GatewayContextHolder.REQUEST, exchange.getRequest());
            GatewayContextHolder.set(GatewayContextHolder.RESPONSE, exchange.getResponse());
            String tag=process.getRequestTag();
            if(StringUtils.isBlank(tag)){
                String _tag=process.currentServerTag();
                if(StringUtils.isNotBlank(_tag)){
                    process.setTag(_tag);
                }
            }
            mono = super.filter(exchange, chain);
        } finally {

            GatewayContextHolder.dispose();
        }
        return mono;
    }

//
}
