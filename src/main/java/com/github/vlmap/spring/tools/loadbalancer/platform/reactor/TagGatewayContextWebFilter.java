package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class TagGatewayContextWebFilter implements GlobalFilter, AbstractReactorContextWebFilter {

    private SpringToolsProperties properties;

    public TagGatewayContextWebFilter( SpringToolsProperties properties) {
         this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        try {
            String tag = exchange.getRequest().getHeaders().getFirst(this.properties.getTagHeaderName());


            if (StringUtils.isBlank(tag)) {
                tag = properties.getTagLoadbalancer().getHeader();
                if (StringUtils.isNotBlank(tag)) {
                    exchange.getRequest().getHeaders().add(this.properties.getTagHeaderName(), tag);
                }
            }
            ReactiveContextHolder.set(exchange);
            return chain.filter(exchange);
        } finally {

            ReactiveContextHolder.dispose();
        }

    }


}
