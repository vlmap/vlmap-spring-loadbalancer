package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重写GatewayLoadBalancerClientAutoConfiguration
 */
@Configuration

@EnableConfigurationProperties({SpringToolsProperties.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)

 public class TagReactiveAutoConfiguration {

    public TagReactiveAutoConfiguration() {
        Platform.getInstnce().setPlatform(Platform.REACTIVE);

    }




    @Bean
    public TagLoadBalancerClientFilterProxy tagLoadBalancerClientFilterProxy(SpringToolsProperties properties) {

        return new TagLoadBalancerClientFilterProxy(properties);
    }

}
