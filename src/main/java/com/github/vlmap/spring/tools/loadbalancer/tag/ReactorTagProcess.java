package com.github.vlmap.spring.tools.loadbalancer.tag;

import com.github.vlmap.spring.tools.loadbalancer.filter.TagContextHolder;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class ReactorTagProcess extends AbstractTagProcess {


    @Override
    public void setTag(String tag) {
        ServerHttpRequest request = TagContextHolder.getRequest();
        if (request != null) {
            request.getHeaders().add(this.properties.getTagHeaderName(), tag);
        }

    }

    @Override
    protected String getRequestTag() {
        ServerHttpRequest request = TagContextHolder.getRequest();
        if (request != null) {
            return request.getHeaders().getFirst(this.properties.getTagHeaderName());
        }
        return null;
    }
}
