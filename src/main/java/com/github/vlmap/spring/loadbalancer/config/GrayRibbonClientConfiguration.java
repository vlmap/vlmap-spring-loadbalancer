package com.github.vlmap.spring.loadbalancer.config;


import com.ecwid.consul.v1.ConsulClient;
import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.util.NamedContextFactoryUtils;
import com.github.vlmap.spring.loadbalancer.core.GrayLoadBalancer;
import com.github.vlmap.spring.loadbalancer.core.GrayClientServer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServerList;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.ConsulServerList;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.List;
import java.util.Set;


@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayRibbonClientConfiguration {

    private String clientName;

    public GrayRibbonClientConfiguration(IClientConfig clientConfig) {

        this.clientName = clientConfig.getClientName();
    }

    @Bean
    public GrayClientServer grayClientServer(ApplicationContext context) {

        GrayClientServer  bean= new GrayClientServer(clientName);
        if(context.getParent()!=null){
           context=context.getParent();
        }
        if(context instanceof AbstractApplicationContext){
            AbstractApplicationContext root=(AbstractApplicationContext) context;
            root.addApplicationListener((EnvironmentChangeEvent event) -> {
                bean.listener(event);
            });
        }
        return bean;
    }


    @Autowired
    public void delegatingLoadBalancer(ILoadBalancer lb,
                                       IRule rule,
                                       GrayClientServer grayClientServer) {


        rule.setLoadBalancer(new GrayLoadBalancer(lb, grayClientServer));

    }


    @Autowired
    public void ribbonClientRefresh(ApplicationContext context, SpringClientFactory clientFactory) {
        ApplicationContext parent = context.getParent();
        String name = clientName + ".";
        if (parent instanceof AbstractApplicationContext) {
            AbstractApplicationContext applicationContext = (AbstractApplicationContext) parent;
            applicationContext.addApplicationListener((EnvironmentChangeEvent e) -> {
                Set<String> keys = e.getKeys();
                if (CollectionUtils.isNotEmpty(keys)) {
                    for (String key : keys) {
                        if (StringUtils.startsWith(key, name)) {
                            NamedContextFactoryUtils.close(clientFactory, clientName);

                            applicationContext.getApplicationListeners().remove(this);
                            break;
                        }
                    }
                }

            });
        }


    }

    @Configuration
    @ConditionalOnClass(ConsulDiscoveryProperties.class)
    @ConditionalOnProperty(name = "spring.cloud.consul.discovery.enabled", matchIfMissing = true)

    static class ConsulServerListConfiguration {
        @Autowired
        private ConsulClient client;

        @Bean
        @ConditionalOnMissingBean

        public ServerList<?> ribbonServerList(IClientConfig config, ConsulDiscoveryProperties properties) {
            AbstractServerList serverList = new ConfigurationBasedServerList();
            serverList.initWithNiwsConfig(config);
            List list = serverList.getInitialListOfServers();
            if (CollectionUtils.isEmpty(list)) {
                serverList = new ConsulServerList(this.client, properties);
                serverList.initWithNiwsConfig(config);
            }

            return serverList;
        }

    }

    @Configuration
    @ConditionalOnClass(NacosDiscoveryProperties.class)
    @ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)

    static class NacosServerListConfiguration {
        @Bean
        @ConditionalOnMissingBean

        public ServerList<?> ribbonServerList(IClientConfig config, NacosDiscoveryProperties properties) {
            AbstractServerList serverList = new ConfigurationBasedServerList();
            serverList.initWithNiwsConfig(config);
            List list = serverList.getInitialListOfServers();
            if (CollectionUtils.isEmpty(list)) {
                serverList = new NacosServerList(properties);
                serverList.initWithNiwsConfig(config);
            }

            return serverList;
        }

    }


}
