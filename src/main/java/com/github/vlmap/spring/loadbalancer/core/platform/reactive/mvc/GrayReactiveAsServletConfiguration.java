package com.github.vlmap.spring.loadbalancer.core.platform.reactive.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)

public class GrayReactiveAsServletConfiguration {


    public  WebFluxRegistrations  webFluxRegistrations(){
        return new GrayWebFluxRegistrations();
    }

}
