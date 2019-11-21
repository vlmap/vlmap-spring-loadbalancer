package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.tag.ReactorTagProcess;
import com.github.vlmap.spring.tools.loadbalancer.tag.TagProcess;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.DispatcherHandler;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})
@EnableAutoConfiguration(exclude = GatewayLoadBalancerClientAutoConfiguration.class)

@Import({TagGatewayLoadBalancerClientAutoConfiguration.class})
public class RibbonClientSpecificationAutoConfiguration {
    @Bean
    public RibbonClientSpecification specification() {
        Class[] classes = new Class[]{TagRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + TagRibbonClientConfiguration.class.getName(), classes);
    }
    @Bean
    @ConditionalOnClass(DispatcherHandler.class)
    public TagProcess reactorTagProcess() {
        return new ReactorTagProcess();


    }


}
