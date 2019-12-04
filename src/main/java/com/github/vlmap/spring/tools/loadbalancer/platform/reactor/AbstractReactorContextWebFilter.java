package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import org.springframework.core.Ordered;

public interface AbstractReactorContextWebFilter extends Ordered {

    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
