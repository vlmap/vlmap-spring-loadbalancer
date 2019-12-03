package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
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


    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String delegatingLoadBalancer(IClientConfig clientConfig,
                                         ILoadBalancer lb,
                                         IRule rule,

                                         @Autowired(required = false) List<TagProcess> tagProcesses,
                                         @Autowired(required = false) DelegatePropChangeListener delegatePropChangeListener) {


        if (lb instanceof BaseLoadBalancer) {
            BaseLoadBalancer target = (BaseLoadBalancer) lb;


            DelegatingLoadBalancer delegating = new DelegatingLoadBalancer(clientConfig, target, tagProcesses);
            delegating.setDelegatePropChangeListener(delegatePropChangeListener);
            rule.setLoadBalancer(delegating);


        }


        return "delegatingLoadBalancer";
    }


}
