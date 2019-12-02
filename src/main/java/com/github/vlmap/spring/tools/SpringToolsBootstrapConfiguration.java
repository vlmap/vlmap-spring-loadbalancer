package com.github.vlmap.spring.tools;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsBootstrapConfiguration implements Ordered {
//ConfigBootstrapConfiguration

    @Bean
    public SpringToolsPropertySourceLocator springToolsPropertySourceLocator(SpringToolsProperties properties){
        return  new SpringToolsPropertySourceLocator(properties);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
