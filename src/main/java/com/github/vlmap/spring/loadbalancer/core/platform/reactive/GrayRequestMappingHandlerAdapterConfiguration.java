package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.method.annotation.GrayRequestMappingHandlerAdapter;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})
@ConditionalOnProperty(name = "vlmap.spring.loadbalancer.controller.enabled", matchIfMissing = true)

@Configuration
public class GrayRequestMappingHandlerAdapterConfiguration {

    @Bean
    public WebFluxRegistrations webFluxRegistrations(GrayLoadBalancerProperties properties) {
        return new GrayWebFluxRegistrations(properties);
    }
    public static class GrayWebFluxRegistrations implements WebFluxRegistrations {
        private GrayLoadBalancerProperties properties;

        public GrayWebFluxRegistrations(GrayLoadBalancerProperties properties) {
            this.properties = properties;
        }

        @Override
        public GrayRequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
            GrayRequestMappingHandlerAdapter handlerAdapter= new GrayRequestMappingHandlerAdapter();
            handlerAdapter.setProperties(properties);

            return handlerAdapter;
        }
    }

}
