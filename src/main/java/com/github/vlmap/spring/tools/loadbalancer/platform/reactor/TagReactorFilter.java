package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


public class TagReactorFilter implements WebFilter, ReactorFilter {
    private SpringToolsProperties properties;

    public TagReactorFilter(SpringToolsProperties properties) {

        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
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
