package com.github.vlmap.spring.tools.loadbalancer.platform.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@ConditionalOnProperty("spring.tools.tag-load-balancer.feign.enabled")
public class TagFeignAutoConfiguration {
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();

    }
}