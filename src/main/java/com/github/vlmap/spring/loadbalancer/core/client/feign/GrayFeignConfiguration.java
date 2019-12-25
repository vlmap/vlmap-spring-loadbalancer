package com.github.vlmap.spring.loadbalancer.core.client.feign;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@ConditionalOnProperty(name = "vlmap.spring.loadbalancer.feign.enabled", matchIfMissing = true)

public class GrayFeignConfiguration {

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