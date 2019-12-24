package com.github.vlmap.spring.loadbalancer.config;


import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;


public class RibbonClientSpecificationAutoConfiguration {


    @Bean
    public RibbonClientSpecification ribbonClientSpecification() {
        Class[] classes = new Class[]{GrayRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + GrayRibbonClientConfiguration.class.getName(), classes);
    }


}
