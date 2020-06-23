package com.github.vlmap.spring.loadbalancer.config;


import com.github.vlmap.spring.loadbalancer.actuate.GrayRouteEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RibbonClientSpecification.class)

public class RibbonClientSpecificationAutoConfiguration {


    @Bean
    public RibbonClientSpecification ribbonClientSpecification() {
        Class[] classes = new Class[]{GrayRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + GrayRibbonClientConfiguration.class.getName(), classes);
    }
    @Configuration
    @ConditionalOnClass({Endpoint.class})
    @ConditionalOnProperty(
            value = {"vlmap.spring.loadbalancer.actuator.enabled"},
            matchIfMissing = true
    )

    static class ActuatorConfiguration {
        @Bean
        public GrayRouteEndpoint loadbalancerEndpoint() {
            return new GrayRouteEndpoint();
        }
    }

}
