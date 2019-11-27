package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration
@ConditionalOnClass({DispatcherHandler.class})


public class TagReactorAutoConfiguration  {
    @Bean
    public ReactorTagProcess reactorTagProcess() {
        return new ReactorTagProcess();

    }

    // GlobalFilter beans

    @Bean
    @ConditionalOnBean
    public TagReactorContextWebFilter reactorContextWebFilter(ReactorTagProcess reactorTagProcess) {
        return new TagReactorContextWebFilter(reactorTagProcess);
    }

}
