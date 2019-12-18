package com.github.vlmap.spring.tools.loadbalancer.platform.servlet;

import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.CompositeRouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayServletAutoConfiguration {
    public GrayServletAutoConfiguration() {
        Platform.getInstnce().setPlatform(Platform.SERVLET);
    }


    @Bean
    public GrayServletFilter graySpringmvcFilter(GrayLoadBalancerProperties properties) {

        return new GrayServletFilter(properties);
    }

    @Configuration
    @AutoConfigureAfter(ZuulServerAutoConfiguration.class)
    @ConditionalOnClass(CompositeRouteLocator.class)
    static class ZuulPlatformConfiguration {
        @Autowired(required = false)
        public void platform(CompositeRouteLocator routeLocator) {

            if (routeLocator != null) {
                Platform.getInstnce().setGatewayService(true);
            }
        }
    }


}
