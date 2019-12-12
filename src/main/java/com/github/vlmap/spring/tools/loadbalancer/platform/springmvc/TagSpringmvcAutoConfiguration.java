package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.CompositeRouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler")
@EnableConfigurationProperties({SpringToolsProperties.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)

public class TagSpringmvcAutoConfiguration {
    public TagSpringmvcAutoConfiguration() {
        Platform.getInstnce().setPlatform(Platform.SERVLET);
    }


    @Bean
    public TagServletFilter tagSpringmvcFilter(SpringToolsProperties properties) {

        return new TagServletFilter(properties);
    }

    @Configuration
    @AutoConfigureAfter(ZuulServerAutoConfiguration.class)
    @ConditionalOnClass(CompositeRouteLocator.class)
    static class ZuulPlatformConfiguration {
        @Autowired(required = false)
        public void platform(CompositeRouteLocator routeLocator) {

            if (routeLocator != null) {
                Platform.getInstnce().setGatewayService(true);
            }
        }
    }


}
