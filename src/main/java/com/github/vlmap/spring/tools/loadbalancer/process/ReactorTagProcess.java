package com.github.vlmap.spring.tools.loadbalancer.process;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.ReactiveContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;

@Order(20)
public class ReactorTagProcess implements TagProcess {
    protected SpringToolsProperties properties;

    public ReactorTagProcess(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getTag() {
        ServerWebExchange exchange=ReactiveContextHolder.get();
        String header=null;
        if(exchange!=null){
            header=    exchange.getRequest().getHeaders().getFirst(this.properties.getTagHeaderName());
        }
        if(StringUtils.isBlank(header)){
            header=properties.getTagLoadbalancer().getHeader();
        }
        return header;
    }
}
