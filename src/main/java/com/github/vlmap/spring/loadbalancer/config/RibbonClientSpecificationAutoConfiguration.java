package com.github.vlmap.spring.loadbalancer.config;


import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.CurrentInstanceMetadataProvider;
import com.netflix.appinfo.EurekaInstanceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;

import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClientSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RibbonClientSpecification.class)
@EnableConfigurationProperties(GrayLoadBalancerProperties.class)

public class RibbonClientSpecificationAutoConfiguration {


    @Bean
    public RibbonClientSpecification ribbonClientSpecification() {
        Class[] classes = new Class[]{GrayRibbonClientConfiguration.class};
        return new RibbonClientSpecification("default." + GrayRibbonClientConfiguration.class.getName(), classes);
    }


    @Configuration
    @ConditionalOnClass(EurekaInstanceConfig.class)

    static class EurekaRegistrationConfiguration {
        @Bean
        @ConditionalOnBean(EurekaInstanceConfig.class)
        public CurrentInstanceMetadataProvider metaDataProvider(EurekaInstanceConfig instanceConfig) {
            return new CurrentInstanceMetadataProvider.EurekaCurrentInstanceMetadataProvider(instanceConfig);

        }
    }

    @Configuration
    @ConditionalOnClass(NacosDiscoveryProperties.class)
    static class NacosRegistrationConfiguration {
        @Bean
        @ConditionalOnBean(NacosDiscoveryProperties.class)
        public CurrentInstanceMetadataProvider metaDataProvider(NacosDiscoveryProperties properties) {

            return new CurrentInstanceMetadataProvider.NacosCurrentInstanceMetadataProvider(properties);
        }
    }

    @Configuration
    @ConditionalOnClass(ConsulDiscoveryProperties.class)
    static class ConsulRegistrationConfiguration {
        @Bean

        @ConditionalOnBean(ConsulDiscoveryProperties.class)
        public CurrentInstanceMetadataProvider metaDataProvider(ConsulDiscoveryProperties properties) {

            return new CurrentInstanceMetadataProvider.ConsulCurrentInstanceMetadataProvider(properties);
        }
    }




}
