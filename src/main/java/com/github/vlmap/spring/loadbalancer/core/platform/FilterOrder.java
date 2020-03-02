package com.github.vlmap.spring.loadbalancer.core.platform;

import org.springframework.core.Ordered;

public interface FilterOrder {
    int RUNTIME_CONTEXT_TAG_FILTER = Ordered.HIGHEST_PRECEDENCE;

    int ORDER_READ_BODY_FILTER = RUNTIME_CONTEXT_TAG_FILTER + 10;

    int ORDER_ATTACH_FILTER = ORDER_READ_BODY_FILTER + 10;

    int ORDER_RESPONDER_FILTER = ORDER_ATTACH_FILTER + 10;

    int ORDER_STRICT_FILTER = ORDER_RESPONDER_FILTER + 10;


}
