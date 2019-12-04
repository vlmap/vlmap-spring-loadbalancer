package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.core.Ordered;

public interface ReactorFilter extends Ordered {
    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

//    default int getOrder() {
//        return LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER-1;
//    }
}
