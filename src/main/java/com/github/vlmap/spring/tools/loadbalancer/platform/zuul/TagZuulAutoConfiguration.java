package com.github.vlmap.spring.tools.loadbalancer.platform.zuul;

import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.loadbalancer.process.ZuulTagProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RibbonRoutingFilter.class)

public class TagZuulAutoConfiguration {
    @Bean
    public TagProcess zuulTagProcess() {
        return new ZuulTagProcess();

    }
    @Bean
    public TagZuulFilter tagZuulFilter(){
        return new  TagZuulFilter();
    }
}
