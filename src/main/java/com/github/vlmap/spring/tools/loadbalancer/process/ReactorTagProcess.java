package com.github.vlmap.spring.tools.loadbalancer.process;

import com.github.vlmap.spring.tools.loadbalancer.platform.gateway.GatewayContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
@Order(20)
public class ReactorTagProcess extends AbstractTagProcess {


    @Override
    public void setTag(String tag) {
        if(StringUtils.isBlank(tag))return;

        ServerHttpRequest request = GatewayContextHolder.getRequest();
        if (request != null) {
            request.getHeaders().add(this.properties.getTagHeaderName(), tag);
        }

    }

    @Override
    public String getRequestTag() {
        ServerHttpRequest request = GatewayContextHolder.getRequest();
        if (request != null) {
            return request.getHeaders().getFirst(this.properties.getTagHeaderName());
        }
        return null;
    }
}
