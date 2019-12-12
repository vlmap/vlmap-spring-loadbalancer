package com.github.vlmap.spring.tools.loadbalancer.client.feign;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.tools.tag-loadbalancer.feign.enabled", matchIfMissing = true)
@EnableConfigurationProperties({SpringToolsProperties.class})

public class TagFeignAutoConfiguration {
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();

    }

    @Configuration

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)

    static class TagFeignClientProxyConfiguration {
        @Bean
        public TagFeignClientProxy feignClientProxy(SpringToolsProperties properties) {
            return new TagFeignClientProxy(properties);
        }
    }
}