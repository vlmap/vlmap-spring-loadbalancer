package com.github.vlmap.spring.tools.loadbalancer.platform.feign;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@AutoConfigureAfter({SpringToolsAutoConfiguration.class,RibbonClientSpecificationAutoConfiguration.class})
@ConditionalOnProperty(name = "spring.tools.tag-loadbalancer.feign.enabled",matchIfMissing = true)

public class TagFeignAutoConfiguration {
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();

    }
}