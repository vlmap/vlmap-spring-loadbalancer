package com.github.vlmap.spring.loadbalancer.config;


import com.ecwid.consul.v1.ConsulClient;
import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.ServerListMetadataProvider;
import com.github.vlmap.spring.loadbalancer.core.route.DefaultInitializingRouteBean;
import com.github.vlmap.spring.loadbalancer.core.route.InitializingRouteBean;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServerList;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.ConsulServerList;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})
@Order(10)
public class GrayRibbonClientConfiguration {
    @Configuration
    static class InitializingRouteConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InitializingRouteBean initializingRouteBean() {
            return new DefaultInitializingRouteBean();
        }
    }
    @Configuration
    @ConditionalOnClass(DomainExtractingServerList.class)
    static class EurekaServerListMetaDataProviderConfiguration {

        @Bean
        public ServerListMetadataProvider serverListMetaDataProvider(ServerList serverList, IClientConfig config) {
            if (serverList instanceof DomainExtractingServerList) {
                return new ServerListMetadataProvider.EurekaServerListMetadataProvider(config);
            }
            return new ServerListMetadataProvider.StaticServerListMetadataProvider(config);

        }
    }

    @Configuration
    @ConditionalOnClass(NacosServerList.class)
    static class NacosServerListMetaDataProviderConfiguration {
        @Bean
        public ServerListMetadataProvider serverListMetaDataProvider(ServerList serverList, IClientConfig config) {
            if (serverList instanceof NacosServerList) {
                return new ServerListMetadataProvider.NacosServerListMetadataProvider(config);
            }
            return new ServerListMetadataProvider.StaticServerListMetadataProvider(config);

        }

    }


    @Configuration
    @ConditionalOnClass(ConsulServerList.class)
    static class ConsulServerListMetaDataProviderConfiguration {
        @Bean
        public ServerListMetadataProvider serverListMetaDataProvider(ServerList serverList, IClientConfig config) {
            if (serverList instanceof ConsulServerList) {
                return new ServerListMetadataProvider.ConsulServerListMetadataProvider(config);
            }
            return new ServerListMetadataProvider.StaticServerListMetadataProvider(config);

        }

    }


    @Configuration
    @ConditionalOnClass(ConsulDiscoveryProperties.class)
    @ConditionalOnProperty(name = "spring.cloud.consul.discovery.enabled", matchIfMissing = true)

    static class ConsulServerListConfiguration {


        /**
         * 先从Environment 读取ServerList，Consul 默认不读取静态ServerList
         *
         * @param config
         * @param properties
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public ServerList<?> ribbonServerList(IClientConfig config, ConsulClient client, ConsulDiscoveryProperties properties, PropertiesFactory propertiesFactory) {
            if (propertiesFactory.isSet(ServerList.class, config.getClientName())) {
                return propertiesFactory.get(ServerList.class, config, config.getClientName());
            }
            ConsulServerList serverList = new ConsulServerList(client, properties);
            serverList.initWithNiwsConfig(config);


            return serverList;
        }

    }

    @Configuration
    @ConditionalOnClass(NacosDiscoveryProperties.class)
    @ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)

    static class NacosServerListConfiguration {
        /**
         * 先从Environment 读取ServerList，Nacos 默认不读取静态ServerList
         *
         * @param config
         * @param properties
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public ServerList<?> ribbonServerList(IClientConfig config, NacosDiscoveryProperties properties, PropertiesFactory propertiesFactory) {
            if (propertiesFactory.isSet(ServerList.class, config.getClientName())) {
                return propertiesFactory.get(ServerList.class, config, config.getClientName());
            }
            NacosServerList serverList = new NacosServerList(properties);
            serverList.initWithNiwsConfig(config);

            return serverList;
        }

    }


}
