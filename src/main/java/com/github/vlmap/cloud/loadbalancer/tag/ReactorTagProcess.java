package com.github.vlmap.cloud.loadbalancer.tag;

 import com.github.vlmap.cloud.loadbalancer.filter.TagContextHolder;
 import org.springframework.http.server.reactive.ServerHttpRequest;

public class ReactorTagProcess extends AbstractTagProcess {


    @Override
    public void setTag(String tag) {
        ServerHttpRequest request = TagContextHolder.getRequest();
        if (request != null) {
            request.getHeaders().add(loadbalancerTag, tag);
        }

    }

    @Override
    protected String getRequestTag() {
        ServerHttpRequest request = TagContextHolder.getRequest();
        if (request != null) {
            return request.getHeaders().getFirst(loadbalancerTag);
        }
        return null;
    }
}
