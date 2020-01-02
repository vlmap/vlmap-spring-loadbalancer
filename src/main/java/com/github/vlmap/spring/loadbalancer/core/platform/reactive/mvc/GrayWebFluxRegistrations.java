package com.github.vlmap.spring.loadbalancer.core.platform.reactive.mvc;

import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

public class GrayWebFluxRegistrations implements WebFluxRegistrations {

    @Override
    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();


    }
}
