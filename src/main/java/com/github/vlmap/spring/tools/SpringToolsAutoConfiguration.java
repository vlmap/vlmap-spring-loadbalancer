package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.actuator.PropertiesEndPoint;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropertiesListener;
import com.github.vlmap.spring.tools.event.listener.RefreshListener;
import com.github.vlmap.spring.tools.loadbalancer.platform.gateway.TagLoadBalancerClientFilterProxy;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsAutoConfiguration {

    @Bean
    public TagLoadBalancerClientFilterProxy tagRequestAop(SpringToolsProperties properties){
        return new TagLoadBalancerClientFilterProxy(properties);
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public PropertiesEndPoint propsEndPoint(Environment environment, SpringToolsProperties properties) {
        return new PropertiesEndPoint(environment, properties);
    }

    @Bean

    public RefreshListener refreshListener() {
        return new RefreshListener();
    }

    @Bean
    public DelegatePropChangeListener delegatePropChangeListener() {
        return new DelegatePropChangeListener();
    }

    @Bean
    public PropertiesListener serverTagListener(SpringToolsProperties properties) {
        ConfigurationPropertyName propertyName = ConfigurationPropertyName.of("spring.tools.tag-loadbalancer.header");
        PropertiesListener listener = new PropertiesListener(propertyName, false, (event -> {

            properties.getTagLoadbalancer().setHeader(event.getValue());


        }));
        return listener;
    }
}
