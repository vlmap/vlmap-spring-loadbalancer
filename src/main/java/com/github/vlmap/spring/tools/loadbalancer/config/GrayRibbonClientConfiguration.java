package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import com.github.vlmap.spring.tools.context.NamedContextFactoryUtils;
import com.github.vlmap.spring.tools.loadbalancer.GrayClientServer;
import com.github.vlmap.spring.tools.loadbalancer.GrayLoadBalancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServerList;
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
    public GrayClientServer grayClientServer() {
        return new GrayClientServer(clientName);
    }
    @Configuration
    @ConditionalOnClass(NacosDiscoveryProperties.class)
    static  class NacosServerListConfiguration{
        @Bean
        @ConditionalOnMissingBean
        public ServerList<?> ribbonServerList(IClientConfig config, NacosDiscoveryProperties nacosDiscoveryProperties) {
            AbstractServerList serverList = new ConfigurationBasedServerList();
            serverList.initWithNiwsConfig(config);
            List list= serverList. getInitialListOfServers();
            if(CollectionUtils.isEmpty(list)){


                serverList = new NacosServerList(nacosDiscoveryProperties);
                serverList.initWithNiwsConfig(config);
            }

            return serverList;
        }

    }

    @Autowired
    public void delegatingLoadBalancer(ILoadBalancer lb,
                                       IRule rule,
                                       GrayClientServer grayClientServer,
                                       GrayLoadBalancerProperties properties) {


        rule.setLoadBalancer(new GrayLoadBalancer(lb, grayClientServer, properties));

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

}
