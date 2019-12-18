package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.context.PropertiesPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration

public class GrayLoadBanacerBootstrapConfiguration implements Ordered {


    @Bean
    public PropertiesPropertySourceLocator springToolsPropertySourceLocator() {
        return new PropertiesPropertySourceLocator("gray-loadbalancer",new GrayLoadBalancerProperties());
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
