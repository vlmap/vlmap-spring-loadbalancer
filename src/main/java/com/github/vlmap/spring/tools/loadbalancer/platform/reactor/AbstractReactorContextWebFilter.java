package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;


import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;


public class AbstractReactorContextWebFilter implements Ordered {
    private ReactorTagProcess process;

    public AbstractReactorContextWebFilter(ReactorTagProcess process) {

        this.process = process;
    }


    public void filter(ServerWebExchange exchange) {

        try {
            ReactiveContextHolder.set(exchange);
            String tag = process.getRequestTag();
            if (StringUtils.isBlank(tag)) {
                tag = process.currentServerTag();
                if (StringUtils.isNotBlank(tag)) {
                    exchange.getRequest().getHeaders().add(this.process.getTagHeaderName(), tag);
                }
            }

        } finally {

            ReactiveContextHolder.dispose();
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


//
}
