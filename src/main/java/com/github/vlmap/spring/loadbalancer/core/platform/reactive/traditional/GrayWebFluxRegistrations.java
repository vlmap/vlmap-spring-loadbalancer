package com.github.vlmap.spring.loadbalancer.core.platform.reactive.traditional;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.web.reactive.result.method.annotation.TraditionalRequestMappingHandlerAdapter;

public class GrayWebFluxRegistrations implements WebFluxRegistrations {
    private GrayLoadBalancerProperties properties;

    public GrayWebFluxRegistrations(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    @Override
    public TraditionalRequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        TraditionalRequestMappingHandlerAdapter  handlerAdapter= new TraditionalRequestMappingHandlerAdapter();
        handlerAdapter.setProperties(properties);

        return handlerAdapter;
    }
}
