package com.github.vlmap.spring.loadbalancer.core.platform;

import org.springframework.core.Ordered;

public interface FilterOrder {
    int ORDER_READ_BODY_FILTER = Ordered.HIGHEST_PRECEDENCE;

    int ORDER_ATTACH_FILTER = ORDER_READ_BODY_FILTER + 10;

    int ORDER_STRICT_FILTER = ORDER_ATTACH_FILTER + 10;

    int ORDER_LOAD_BALANCER_CLIENT_FILTER = ORDER_ATTACH_FILTER + 10;

}
