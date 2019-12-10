package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

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
}
