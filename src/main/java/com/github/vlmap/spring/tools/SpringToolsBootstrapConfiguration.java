package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.actuator.PropsEndPoint;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.RefreshListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsBootstrapConfiguration {
//ConfigBootstrapConfiguration

    @Bean
    public SpringToolsPropertySourceLocator springToolsPropertySourceLocator(SpringToolsProperties properties){
        return  new SpringToolsPropertySourceLocator(properties);
    }

}
