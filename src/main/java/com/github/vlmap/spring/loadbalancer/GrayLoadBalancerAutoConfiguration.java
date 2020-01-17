package com.github.vlmap.spring.loadbalancer;

import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.RequestMappingInvoker;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.archaius.ConfigurableEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayLoadBalancerAutoConfiguration {


    @Bean
    public StrictHandler strictHandler(CurrentServer currentService, GrayLoadBalancerProperties properties) {
        return new StrictHandler(properties, currentService);
    }


    @Bean
    public RequestMappingInvoker requestMappingInvoker(GrayLoadBalancerProperties properties) {
        return new RequestMappingInvoker(properties);
    }


    /**
     * configuration在这个作为依赖， configuration初始化后再构造  CurrentServer对象
     *
     * @param configuration
     * @param environment
     * @param inetUtils
     * @return
     */
    @Bean

    public CurrentServer currentService(ConfigurableEnvironmentConfiguration configuration, Environment environment, InetUtils inetUtils) {

        return new CurrentServer(environment, inetUtils);
    }


    @Configuration
    @ConditionalOnClass(HystrixRequestVariable.class)
    static class HystrixConfiguration {
        public HystrixConfiguration() {
            Platform.getInstnce().setHystrix(true);
        }
    }

}
