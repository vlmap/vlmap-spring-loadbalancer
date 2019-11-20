package com.github.vlmap.cloud.loadbalancer.config;


import com.github.vlmap.cloud.loadbalancer.tag.ReactorTagProcess;
import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureOrder(AutoConfigureOrder.DEFAULT_ORDER-100)
public class TagRuleAutoConfiguration {


//    @Bean
//    public PropertiesFactory propertiesFactory(){
//        return new com.github.vlmap.cloud.loadbalancer.ribbon.PropertiesFactory();
//    }


    @Bean
    public TagProcess reactorTagProcess() {
        return new ReactorTagProcess();

    }

    //    @Bean
//    @ConditionalOnClass(RequestContextHolder.class)
//    public TagProcess servletTagProcess(){
//        return new ServletTagProcess();
//
//    }
//    @Bean
//    public TagProcess reactorTagProcess() {
//        return new ReactorTagProcess();
//
//    }


}
