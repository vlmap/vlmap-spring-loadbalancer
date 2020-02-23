package com.github.vlmap.spring.loadbalancer;

import com.github.vlmap.spring.loadbalancer.actuate.loadbalancer.GrayLoadbalancerEndpoint;
import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayLoadBalancerAutoConfiguration {

    @Bean

    public CurrentServer currentService(ConfigurableEnvironment environment ) {

        return new CurrentServer(environment );
    }

    @Bean
    public StrictHandler strictHandler(CurrentServer currentService, GrayLoadBalancerProperties properties) {


        return new StrictHandler(properties, currentService);
    }


    @Configuration
    @ConditionalOnClass(HystrixRequestVariable.class)
    static class HystrixConfiguration {
        public HystrixConfiguration() {
            Platform.getInstnce().setHystrix(true);
        }
    }

    @Configuration
    @ConditionalOnClass({Endpoint.class})
    @ConditionalOnProperty(
            value = { "vlmap.spring.loadbalancer.actuator.enabled"},
            matchIfMissing = true
    )

    static class ActuatorConfiguration {
        @Bean
        public GrayLoadbalancerEndpoint loadbalancerEndpoint() {
            return new GrayLoadbalancerEndpoint();
        }
    }




}
