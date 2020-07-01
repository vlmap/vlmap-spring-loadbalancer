package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration

@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class ReactiveConfiguration {


    @Bean
    public ReadBodyWebFilter readBodyFilter(GrayLoadBalancerProperties properties) {
        return new ReadBodyWebFilter(properties);
    }

    @Bean
    public AttacherWebFilter attacherFilter(GrayLoadBalancerProperties properties) {
        return new AttacherWebFilter(properties);
    }

    @Bean
    public ResponderWebFilter responderFilter(GrayLoadBalancerProperties properties) {

        return new ResponderWebFilter(properties);
    }

    @Bean
    public StrictWebFilter strictFilter(GrayLoadBalancerProperties properties) {
        return new StrictWebFilter(properties);
    }


    @Bean
    public LoadBalancerClientFilterProxy loadBalancerClientFilterProxy(GrayLoadBalancerProperties properties) {

        return new LoadBalancerClientFilterProxy(properties);
    }





}
