package com.github.vlmap.spring.loadbalancer.config;

import com.github.vlmap.spring.loadbalancer.actuate.GrayRouteEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        value = {"vlmap.spring.loadbalancer.actuator.enabled"},
        matchIfMissing = true
)
public class ActuatorConfiguration {
//    @Configuration
//    @ConditionalOnClass({Endpoint.class})
//    static class Actuator_1_Configuration {
//        @Bean
//        public GrayRouteEndpoint loadbalancerEndpoint() {
//            return new GrayRouteEndpoint();
//        }
//    }
    @Configuration
    @ConditionalOnClass({Endpoint.class})
    static class Actuator_2_Configuration {
        @Bean
        public GrayRouteEndpoint loadbalancerEndpoint() {
            return new GrayRouteEndpoint();
        }
    }

}
