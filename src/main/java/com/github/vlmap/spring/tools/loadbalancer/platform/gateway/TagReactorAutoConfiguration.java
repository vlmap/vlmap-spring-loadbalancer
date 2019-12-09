package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.client.feign.TagFeignClientProxy;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration
@ConditionalOnClass({DispatcherHandler.class})
@EnableConfigurationProperties({SpringToolsProperties.class})

@AutoConfigureAfter({ RibbonClientSpecificationAutoConfiguration.class})
public class TagReactorAutoConfiguration {
    @Autowired
    public void setPlatform(DispatcherHandler dispatcherHandler) {
        Platform.getInstnce().setPlatform(Platform.WEBFLUX);
    }

    @Bean
    public TagLoadBalancerClientFilterProxy loadBlancerBeanPostProcessor(SpringToolsProperties properties) {

        return new TagLoadBalancerClientFilterProxy(properties);
    }

}
