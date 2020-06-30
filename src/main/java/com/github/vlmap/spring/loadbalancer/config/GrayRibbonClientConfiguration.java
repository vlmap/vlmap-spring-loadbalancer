package com.github.vlmap.spring.loadbalancer.config;


import com.ecwid.consul.v1.ConsulClient;
import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.GrayLoadBalancer;
import com.github.vlmap.spring.loadbalancer.core.registration.*;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ServerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServerList;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.ConsulServerList;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})

public class GrayRibbonClientConfiguration {


    @Autowired
    private  ILoadBalancer lb;
    @Autowired
    private   IRule rule;
    @Autowired
    private   ServerList serverList;

    @PostConstruct
    public void initLoadBalancer() {

        //替换默认ILoadBalancer为GrayLoadBalancer

        GrayInfoTransform transform = null;
        IClientConfig config = null;


        if (lb instanceof BaseLoadBalancer) {
            config = ((BaseLoadBalancer) lb).getClientConfig();

        }

        String clazz = serverList.getClass().getName();
        if (clazz.equals("org.springframework.cloud.alibaba.nacos.ribbon.NacosServerList")) {
            transform = new NacosGrayInfoTransform(config);

        } else if (clazz.equals("org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList")) {
            transform = new EurekaGrayInfoTransform(config);

        } else if (clazz.equals("org.springframework.cloud.consul.discovery.ConsulServerList")) {
            transform = new ConsulGrayInfoTransform(config);

        } else {
            transform = new StaticGrayInfoTransform(config);
        }


        rule.setLoadBalancer(new GrayLoadBalancer(lb, transform));

    }

//
//    @Autowired
//    public void ribbonClientRefresh(ApplicationContext context, SpringClientFactory clientFactory) {
//        ApplicationContext parent = context.getParent();
//        String name = clientName + ".";
//        if (parent instanceof AbstractApplicationContext) {
//            AbstractApplicationContext applicationContext = (AbstractApplicationContext) parent;
//            applicationContext.addApplicationListener((EnvironmentChangeEvent e) -> {
//                Set<String> keys = e.getKeys();
//                if (CollectionUtils.isNotEmpty(keys)) {
//                    for (String key : keys) {
//                        if (StringUtils.startsWith(key, name)) {
//                            NamedContextFactoryUtils.close(clientFactory, clientName);
//
//                            applicationContext.getApplicationListeners().remove(this);
//                            break;
//                        }
//                    }
//                }
//
//            });
//        }
//
//
//    }

    @Configuration
    @ConditionalOnClass(ConsulDiscoveryProperties.class)
    @ConditionalOnProperty(name = "spring.cloud.consul.discovery.enabled", matchIfMissing = true)

    static class ConsulServerListConfiguration {
        @Autowired
        private ConsulClient client;

        /**
         * 先从Environment 读取ServerList，Consul 默认不读取静态ServerList
         *
         * @param config
         * @param properties
         * @return
         */
        @Bean
        @ConditionalOnMissingBean
        public ServerList<?> ribbonServerList(IClientConfig config, ConsulDiscoveryProperties properties, PropertiesFactory propertiesFactory) {
            if (propertiesFactory.isSet(ServerList.class, config.getClientName())) {
                return propertiesFactory.get(ServerList.class, config, config.getClientName());
            }
            ConsulServerList serverList = new ConsulServerList(this.client, properties);
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
