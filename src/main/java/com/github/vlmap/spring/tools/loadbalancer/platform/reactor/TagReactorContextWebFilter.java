package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


public class TagReactorContextWebFilter extends  AbstractReactorContextWebFilter implements WebFilter {

    public TagReactorContextWebFilter(ReactorTagProcess process) {
        super(process);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        filter(exchange);
        return chain.filter(exchange);
    }

}
