package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.ReactorFilter;
import com.github.vlmap.spring.tools.loadbalancer.platform.zuul.TagZuulAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.zuul.TagZuulFilter;
import com.github.vlmap.spring.tools.loadbalancer.process.SpringmvcTagProcess;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RibbonRoutingFilter.class)
@AutoConfigureAfter({TagZuulAutoConfiguration.class, SpringToolsAutoConfiguration.class})

public class TagSpringmvcAutoConfiguration {
    @Bean
    public SpringmvcTagProcess springmvcTagProcess(SpringToolsProperties properties) {
        return new SpringmvcTagProcess(properties);

    }

    @Bean
    @ConditionalOnMissingBean({TagZuulFilter.class, ReactorFilter.class})
    public TagSpringmvcFilter tagSpringmvcFilter(SpringToolsProperties properties) {
        return new TagSpringmvcFilter(properties);
    }
}
