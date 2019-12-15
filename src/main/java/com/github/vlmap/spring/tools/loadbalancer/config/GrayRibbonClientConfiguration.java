package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.NamedContextFactoryUtils;
import com.github.vlmap.spring.tools.loadbalancer.GrayClientServer;
import com.github.vlmap.spring.tools.loadbalancer.GrayLoadBalancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Set;


@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class GrayRibbonClientConfiguration {

    private String clientName;

    public GrayRibbonClientConfiguration(IClientConfig clientConfig) {

        this.clientName = clientConfig.getClientName();
    }


    @Autowired
    public void delegatingLoadBalancer(ILoadBalancer lb,
                                       IRule rule,

                                       SpringToolsProperties properties) {
        GrayClientServer tagsProvider = new GrayClientServer(clientName);
        tagsProvider.refresh();
        GrayLoadBalancer delegating = new GrayLoadBalancer(tagsProvider, properties);


        delegating.setTarget(lb);


        rule.setLoadBalancer(delegating);

    }


    @Autowired
    public void ribbonClientRefresh(ApplicationContext context,SpringClientFactory clientFactory ) {
        ApplicationContext parent=  context.getParent();
        String name = clientName + ".";
        if(parent instanceof AbstractApplicationContext){
            AbstractApplicationContext applicationContext=(AbstractApplicationContext)parent;
            applicationContext.addApplicationListener((EnvironmentChangeEvent e)->{
                Set<String> keys=e.getKeys();
                if(CollectionUtils.isNotEmpty(keys)){
                    for(String key:keys){
                        if(StringUtils.startsWith(key,name)){
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
