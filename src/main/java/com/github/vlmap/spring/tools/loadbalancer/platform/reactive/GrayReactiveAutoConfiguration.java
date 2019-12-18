package com.github.vlmap.spring.tools.loadbalancer.platform.reactive;

import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayReactiveAutoConfiguration {

    public GrayReactiveAutoConfiguration() {
        Platform.getInstnce().setPlatform(Platform.REACTIVE);

    }

    @Bean
    public GrayStrictReactiveWebFilter grayCompatibleReactiveWebFilter(GrayLoadBalancerProperties properties) {
        return new GrayStrictReactiveWebFilter(properties);
    }


    @Bean
    public GrayLoadBalancerClientFilterProxy grayLoadBalancerClientFilterProxy(GrayLoadBalancerProperties properties) {

        return new GrayLoadBalancerClientFilterProxy(properties);
    }

    @Configuration
    @ConditionalOnClass(LoadBalancerClientFilter.class)
    @AutoConfigureAfter(GatewayAutoConfiguration.class)

    static public class GatewayPlatformConfiguration {

        @Autowired(required = false)

        public void platform(LoadBalancerClientFilter loadBalancerClientFilter) {
            if (loadBalancerClientFilter != null) {
                Platform.getInstnce().setGatewayService(true);
            }


        }
    }

}
