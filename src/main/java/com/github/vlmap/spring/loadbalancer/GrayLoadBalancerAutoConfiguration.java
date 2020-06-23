package com.github.vlmap.spring.loadbalancer;

import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayLoadBalancerAutoConfiguration {


    @Configuration
    @ConditionalOnClass(HystrixRequestVariable.class)
    static class HystrixConfiguration {
        public HystrixConfiguration() {
            Platform.getInstnce().setHystrix(true);
        }
    }




}
