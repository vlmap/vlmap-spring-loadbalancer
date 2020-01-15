package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ReactiveAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayReactiveConfiguration {

    public GrayReactiveConfiguration() {
        Platform.getInstnce().setPlatform(Platform.REACTIVE);

    }

    @Bean
    public GrayStrictReactiveWebFilter strictFilter(StrictHandler strictHandler, GrayLoadBalancerProperties properties) {
        return new GrayStrictReactiveWebFilter(properties, strictHandler);
    }
    @Bean
    public ReactiveAttachHandler attachHandler(Environment environment, GrayLoadBalancerProperties properties) {
        return new ReactiveAttachHandler(  properties,environment);
    }
    @Bean
    public GrayAttachReactiveWebFilter attachFilter(ReactiveAttachHandler attachHandler, GrayLoadBalancerProperties properties) {
        return new GrayAttachReactiveWebFilter(properties, attachHandler);
    }

    @Bean
    public GrayLoadBalancerClientFilterProxy grayLoadBalancerClientFilterProxy(GrayLoadBalancerProperties properties) {

        return new GrayLoadBalancerClientFilterProxy(properties);
    }


}
