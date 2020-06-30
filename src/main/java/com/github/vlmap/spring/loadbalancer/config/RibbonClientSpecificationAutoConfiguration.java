package com.github.vlmap.spring.loadbalancer.config;


import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RibbonClientSpecification.class)
@EnableConfigurationProperties(GrayLoadBalancerProperties.class)

public class RibbonClientSpecificationAutoConfiguration {


    @Bean
    public RibbonClientSpecification ribbonClientSpecification() {
        Class[] classes = new Class[]{GrayRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + GrayRibbonClientConfiguration.class.getName(), classes);
    }


}
