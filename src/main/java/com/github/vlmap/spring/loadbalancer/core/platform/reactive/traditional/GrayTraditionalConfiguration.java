package com.github.vlmap.spring.loadbalancer.core.platform.reactive.traditional;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

@Configuration
public class GrayTraditionalConfiguration {

    @Bean
    public WebFluxRegistrations webFluxRegistrations(GrayLoadBalancerProperties properties) {
        return new GrayWebFluxRegistrations(properties);
    }

}
