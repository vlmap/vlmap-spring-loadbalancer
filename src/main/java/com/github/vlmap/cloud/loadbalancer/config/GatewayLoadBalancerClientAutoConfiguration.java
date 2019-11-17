package com.github.vlmap.cloud.loadbalancer.config;

import com.github.vlmap.cloud.loadbalancer.filter.LoadBalancerContextClientFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

@Configuration
@EnableAutoConfiguration(exclude = org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration.class)

@ConditionalOnClass({ LoadBalancerClient.class, RibbonAutoConfiguration.class,
        DispatcherHandler.class })
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@EnableConfigurationProperties(LoadBalancerProperties.class)
@AutoConfigureOrder(AutoConfigureOrder.DEFAULT_ORDER-100)

public class GatewayLoadBalancerClientAutoConfiguration {

    // GlobalFilter beans

    @Bean
    @ConditionalOnBean(LoadBalancerClient.class)
    @ConditionalOnMissingBean(LoadBalancerClientFilter.class)
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client,
                                                             LoadBalancerProperties properties) {
        return new LoadBalancerContextClientFilter(client, properties);
    }

}
