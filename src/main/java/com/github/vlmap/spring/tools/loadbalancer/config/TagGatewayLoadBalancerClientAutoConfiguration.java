package com.github.vlmap.spring.tools.loadbalancer.config;

import com.github.vlmap.spring.tools.loadbalancer.filter.TagLoadBalancerClientFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration
@ConditionalOnClass({LoadBalancerClient.class, RibbonAutoConfiguration.class,
        DispatcherHandler.class})
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@EnableConfigurationProperties(LoadBalancerProperties.class)
public class TagGatewayLoadBalancerClientAutoConfiguration extends GatewayLoadBalancerClientAutoConfiguration {


    // GlobalFilter beans

    @Bean
    @ConditionalOnBean(LoadBalancerClient.class)
    @ConditionalOnMissingBean(LoadBalancerClientFilter.class)
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client,
                                                             LoadBalancerProperties properties) {
        return new TagLoadBalancerClientFilter(client, properties);
    }

}
