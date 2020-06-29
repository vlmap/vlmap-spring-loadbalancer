package com.github.vlmap.spring.loadbalancer.config;

import com.github.vlmap.spring.loadbalancer.actuate.GrayParamater;
import com.github.vlmap.spring.loadbalancer.actuate.GrayOldEndpoint;
import com.github.vlmap.spring.loadbalancer.actuate.GrayEndpoint;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
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
    @Bean

    public GrayParamater GrayParamater() {
        return new GrayParamater();
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class Actuator_1_Configuration {
        @Bean
        public GrayOldEndpoint loadbalancerEndpoint() {
            return new GrayOldEndpoint();
        }
    }
    @Configuration
    @ConditionalOnClass({Endpoint.class})
    static class Actuator_2_Configuration {
        @Bean
        public GrayEndpoint loadbalancerEndpoint() {
            return new GrayEndpoint();
        }
    }

}
