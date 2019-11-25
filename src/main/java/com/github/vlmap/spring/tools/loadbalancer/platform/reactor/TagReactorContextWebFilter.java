package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


public class TagReactorContextWebFilter implements WebFilter, Ordered {
     private ReactorTagProcess process;

    public TagReactorContextWebFilter(ReactorTagProcess process) {

        this.process=process;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        Mono<Void> mono = null;
        try {
            ReactiveContextHolder.set(ReactiveContextHolder.REQUEST, exchange.getRequest());
            ReactiveContextHolder.set(ReactiveContextHolder.RESPONSE, exchange.getResponse());
            String tag=process.getRequestTag();
            if(StringUtils.isBlank(tag)){
                String _tag=process.currentServerTag();
                if(StringUtils.isNotBlank(_tag)){
                    process.setTag(_tag);
                }
            }
            mono =chain.filter(exchange);
        } finally {

            ReactiveContextHolder.dispose();
        }
        return mono;
    }

    @Override
    public int getOrder() {
        return 0;
    }

//
}
