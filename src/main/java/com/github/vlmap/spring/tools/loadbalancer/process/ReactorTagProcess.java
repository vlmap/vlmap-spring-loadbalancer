package com.github.vlmap.spring.tools.loadbalancer.process;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.ReactiveContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;

@Order(20)
public class ReactorTagProcess extends AbstractTagProcess {

    public ReactorTagProcess(DynamicToolProperties properties) {
        super(properties);
    }

    @Override
    public String getRequestTag() {
        ServerWebExchange exchange=ReactiveContextHolder.get();
        if(exchange!=null){
         return    exchange.getRequest().getHeaders().getFirst(this.properties.getTagHeaderName());
        }

        return null;
    }
}
