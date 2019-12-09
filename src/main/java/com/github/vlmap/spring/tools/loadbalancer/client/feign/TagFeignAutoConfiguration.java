package com.github.vlmap.spring.tools.loadbalancer.client.feign;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;


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
    @ConditionalOnClass({DispatcherHandler.class})
    static class TagFeignClientProxyConfiguration {
        @Bean
        public TagFeignClientProxy feignClientProxy(SpringToolsProperties properties) {
            return new TagFeignClientProxy(properties);
        }
    }
}