package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class ReactiveConfiguration {

    public ReactiveConfiguration() {
        Platform.getInstnce().setPlatform(Platform.REACTIVE);

    }


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
