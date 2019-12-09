package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler")
@EnableConfigurationProperties({SpringToolsProperties.class})

public class TagSpringmvcAutoConfiguration {

    @Autowired
    public void setPlatform(DispatcherServlet servlet) {
        Platform.getInstnce().setPlatform(Platform.SERVLET);
    }

    @Bean
    public TagSpringmvcFilter tagSpringmvcFilter(SpringToolsProperties properties) {

        return new TagSpringmvcFilter(properties);
    }
}
