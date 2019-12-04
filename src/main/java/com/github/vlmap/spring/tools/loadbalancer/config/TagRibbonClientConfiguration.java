package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropertiesListener;
import com.github.vlmap.spring.tools.loadbalancer.DelegatingLoadBalancer;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class TagRibbonClientConfiguration {


    DelegatingLoadBalancer delegating = new DelegatingLoadBalancer();

    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String delegatingLoadBalancer(IClientConfig clientConfig,
                                         ILoadBalancer lb,
                                         IRule rule,

                                         @Autowired(required = false) List<TagProcess> tagProcesses) {
        if(lb instanceof BaseLoadBalancer){

        }
        delegating.setClientConfig(clientConfig);
        delegating.setTagProcesses(tagProcesses);
        delegating.setTarget(lb);

        delegating.tagStateInProgress();

        rule.setLoadBalancer(delegating);
        return "delegatingLoadBalancer";
    }

    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String listener(IClientConfig clientConfig, @Autowired(required = false) DelegatePropChangeListener delegatePropChangeListener) {

        PropertiesListener listener = new PropertiesListener(clientConfig.getClientName(), true, (PropertyChangeEvent event) -> {

            delegating.tagStateInProgress();


        });
        delegatePropChangeListener.addListener(listener);


        return "listener";
    }

}
