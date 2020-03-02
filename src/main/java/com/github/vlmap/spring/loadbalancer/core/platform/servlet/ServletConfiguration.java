package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.platform.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class ServletConfiguration {
    public ServletConfiguration() {
        Platform.getInstnce().setPlatform(Platform.SERVLET);
    }


    @Bean
    public ReadBodyFilter readBodyFilter(GrayLoadBalancerProperties properties) {
        return new ReadBodyServletFilter(properties);
    }

    @Bean
    public AttacherFilter attacherFilter(GrayLoadBalancerProperties properties) {

        return new AttacherServletFilter(properties);
    }

    @Bean
    public ResponderFilter responderFilter(GrayLoadBalancerProperties properties) {

        return new ResponderServletFilter(properties);
    }

    @Bean
    public StrictFilter strictFilter(GrayLoadBalancerProperties properties, CurrentServer currentServer) {
        return new StrictServletFilter(properties, currentServer);
    }

    @Bean
    public RuntimeRouteTagServletFilter runtimeRouteTagServletFilter(GrayLoadBalancerProperties properties) {

        return new RuntimeRouteTagServletFilter(properties);
    }

}
