package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.actuator.PropertiesEndPoint;
import com.github.vlmap.spring.tools.common.AntPathMatcherUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsAutoConfiguration {

    @Autowired
    public void defaultIgnoreUrl(Environment environment) {
        Set<String> urls = new LinkedHashSet<>();
        urls.add("/webjars/**");
        urls.add("/favicon.ico");
        String uri = environment.getProperty("management.endpoints.web.base-path");
        String antPath = AntPathMatcherUtils.toAntPath(uri);

        if (StringUtils.isNotBlank(antPath)) {
            urls.add(antPath);
        }
        SpringToolsProperties.Compatible.defaultIgnoreUrls(new ArrayList<>(urls));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public PropertiesEndPoint propsEndPoint(Environment environment, SpringToolsProperties properties) {
        return new PropertiesEndPoint(environment, properties);
    }


}
