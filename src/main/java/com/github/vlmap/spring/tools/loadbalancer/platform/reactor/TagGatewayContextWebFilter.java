package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class TagGatewayContextWebFilter extends AbstractReactorContextWebFilter implements GlobalFilter {

    public TagGatewayContextWebFilter(ReactorTagProcess process) {
        super(process);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        filter(exchange);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
