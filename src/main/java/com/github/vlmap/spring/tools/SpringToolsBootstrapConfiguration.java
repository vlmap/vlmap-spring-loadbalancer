package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.context.SpringToolsPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration

public class SpringToolsBootstrapConfiguration implements Ordered {


    @Bean
    public SpringToolsPropertySourceLocator springToolsPropertySourceLocator() {
        return new SpringToolsPropertySourceLocator();
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
