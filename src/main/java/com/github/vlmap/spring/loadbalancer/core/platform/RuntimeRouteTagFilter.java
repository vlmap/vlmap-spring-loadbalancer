package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;


public class RuntimeRouteTagFilter implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GrayLoadBalancerProperties properties;

    public RuntimeRouteTagFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }


    @Override
    public int getOrder() {
        return FilterOrder.RUNTIME_CONTEXT_TAG_FILTER;
    }

}
