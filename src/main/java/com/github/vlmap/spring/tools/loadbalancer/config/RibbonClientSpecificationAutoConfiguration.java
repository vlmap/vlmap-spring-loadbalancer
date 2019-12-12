package com.github.vlmap.spring.tools.loadbalancer.config;


import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;


public class RibbonClientSpecificationAutoConfiguration {


    @Bean
    public RibbonClientSpecification specification() {
        Class[] classes = new Class[]{TagRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + TagRibbonClientConfiguration.class.getName(), classes);
    }


}
