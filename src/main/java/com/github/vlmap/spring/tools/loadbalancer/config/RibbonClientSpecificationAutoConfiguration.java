package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.loadbalancer.process.SpringMVCTagProcess;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})
@EnableAutoConfiguration(exclude = {GatewayLoadBalancerClientAutoConfiguration.class})
public class RibbonClientSpecificationAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean

    public DynamicToolProperties dynamicToolProperties(Environment env, SpringToolsProperties properties) {
        return new DynamicToolProperties(env, properties);
    }

    @Bean
    public RibbonClientSpecification specification() {
        Class[] classes = new Class[]{TagRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + TagRibbonClientConfiguration.class.getName(), classes);
    }

    @ConditionalOnClass(org.springframework.web.servlet.DispatcherServlet.class)
    @Configuration
    static class SpringMVCConfiguration {
        @Bean
        public SpringMVCTagProcess SpringMVCTagProcess() {
            return new SpringMVCTagProcess();
        }
    }


}
