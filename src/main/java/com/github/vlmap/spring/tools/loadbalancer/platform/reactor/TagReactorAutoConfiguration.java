package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
 import org.springframework.boot.autoconfigure.condition.*;

import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration
@ConditionalOnClass({DispatcherHandler.class})

@AutoConfigureAfter({SpringToolsAutoConfiguration.class,RibbonClientSpecificationAutoConfiguration.class})
public class TagReactorAutoConfiguration  {
    @Bean
    public ReactorTagProcess reactorTagProcess(DynamicToolProperties properties) {
        return new ReactorTagProcess(properties);

    }


    @Configuration
    @ConditionalOnProperty(name = "spring.cloud.gateway.enabled", matchIfMissing = true)
    @ConditionalOnClass(GatewayAutoConfiguration.class)
     static public class GatewayFilterConfiguration{
        @Bean
        @ConditionalOnMissingBean(AbstractReactorContextWebFilter.class)

        public AbstractReactorContextWebFilter reactorContextWebFilter(ReactorTagProcess reactorTagProcess) {
            return new TagGatewayContextWebFilter(reactorTagProcess);
        }
    }
    @Configuration

    @ConditionalOnMissingClass("org.springframework.cloud.gateway.config.GatewayAutoConfiguration")

    static public class ReactorFilterConfiguration{
        @Bean
        @ConditionalOnMissingBean(AbstractReactorContextWebFilter.class)
        public AbstractReactorContextWebFilter reactorContextWebFilter(ReactorTagProcess reactorTagProcess) {
            return new TagReactorContextWebFilter(reactorTagProcess);
        }
    }
}
