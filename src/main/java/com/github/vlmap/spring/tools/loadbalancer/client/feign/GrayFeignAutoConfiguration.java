package com.github.vlmap.spring.tools.loadbalancer.client.feign;

import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.tools.feign.enabled", matchIfMissing = true)

public class GrayFeignAutoConfiguration {
    @Bean
    public GrayFeignRequestInterceptor feignRequestInterceptor() {
        return new GrayFeignRequestInterceptor();

    }

    @Configuration

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @EnableConfigurationProperties({GrayLoadBalancerProperties.class})

    static class TagFeignClientProxyConfiguration {
        @Bean
        public GrayFeignClientProxy feignClientProxy(GrayLoadBalancerProperties properties) {
            return new GrayFeignClientProxy(properties);
        }
    }
}