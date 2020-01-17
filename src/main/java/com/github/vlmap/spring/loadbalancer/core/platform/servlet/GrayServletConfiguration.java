package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ServletAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayServletConfiguration {
    public GrayServletConfiguration() {
        Platform.getInstnce().setPlatform(Platform.SERVLET);
    }


    @Bean
    public GrayStrictServletFilter strictServletFilter(StrictHandler strictHandler, GrayLoadBalancerProperties properties) {

        return new GrayStrictServletFilter(properties, strictHandler);
    }

    @Bean
    public ServletAttachHandler attachHandler(Environment environment, GrayLoadBalancerProperties properties) {
        return new ServletAttachHandler(properties, environment);
    }

    @Bean
    public GrayAttachServletFilter attachServletFilter(ServletAttachHandler attachHandler, GrayLoadBalancerProperties properties) {

        return new GrayAttachServletFilter(properties, attachHandler);
    }

    @Bean
    public GrayLoadBalancerContextServletFilter loadBalancerContextServletFilter(GrayLoadBalancerProperties properties) {

        return new GrayLoadBalancerContextServletFilter(properties);
    }

}
