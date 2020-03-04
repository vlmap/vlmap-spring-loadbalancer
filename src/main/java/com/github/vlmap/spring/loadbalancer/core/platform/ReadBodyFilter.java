package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;


public abstract class ReadBodyFilter implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GrayLoadBalancerProperties properties;
    public static final String READ_BODY_TAG = "__ReadBodyTag__";

    public ReadBodyFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}
