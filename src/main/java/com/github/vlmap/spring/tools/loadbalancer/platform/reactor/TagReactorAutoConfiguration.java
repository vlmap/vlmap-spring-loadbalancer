package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.process.ReactorTagProcess;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration
@ConditionalOnClass({DispatcherHandler.class})
@EnableConfigurationProperties({SpringToolsProperties.class})

@AutoConfigureAfter({SpringToolsAutoConfiguration.class,RibbonClientSpecificationAutoConfiguration.class})
public class TagReactorAutoConfiguration  {


    @Bean
    @ConditionalOnMissingBean

    public DynamicToolProperties dynamicToolProperties(Environment env, SpringToolsProperties properties) {
        return new DynamicToolProperties(env, properties);
    }
    @Bean
    public ReactorTagProcess reactorTagProcess(SpringToolsProperties properties) {
        return new ReactorTagProcess(properties);

    }


    @Configuration
    @ConditionalOnProperty(name = "spring.cloud.gateway.enabled", matchIfMissing = true)
    @ConditionalOnClass(GatewayAutoConfiguration.class)
     static public class GatewayFilterConfiguration{
        @Bean
        @ConditionalOnMissingBean(AbstractReactorContextWebFilter.class)

        public AbstractReactorContextWebFilter reactorContextWebFilter(SpringToolsProperties properties) {
            return new TagGatewayContextWebFilter(properties);
        }
    }
    @Configuration

    @ConditionalOnMissingClass("org.springframework.cloud.gateway.config.GatewayAutoConfiguration")

    static public class ReactorFilterConfiguration{
        @Bean
        @ConditionalOnMissingBean(AbstractReactorContextWebFilter.class)
        public AbstractReactorContextWebFilter reactorContextWebFilter(SpringToolsProperties properties) {
            return new TagReactorContextWebFilter(properties);
        }
    }
}
