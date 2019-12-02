package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropChangeListener;
import com.github.vlmap.spring.tools.loadbalancer.DelegatingLoadBalancer;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

//@EnableConfigurationProperties({SpringToolsProperties.class})

@Configuration
public class TagRibbonClientConfiguration {

    @Autowired

    private Environment env;


    @Bean
    public AtomicReference<Map<Server, String>> tagsInProgress() {

        return new AtomicReference<Map<Server, String>>(Collections.emptyMap());
    }


    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String tagStateProgress( IClientConfig clientConfig,
                                    ILoadBalancer lb,
                                    IRule rule,
                                    AtomicReference<Map<Server, String>> tagsInProgress,
                                    @Autowired(required = false) List<TagProcess> tagProcesses,
                                    @Autowired(required = false) DelegatePropChangeListener delegatePropChangeListener) {


        if (lb instanceof BaseLoadBalancer) {
            BaseLoadBalancer target = (BaseLoadBalancer) lb;
            target.addServerListChangeListener((oldList, newList) -> this.tagStateInProgress(clientConfig, newList, tagsInProgress));
            target.addServerStatusChangeListener(servers -> this.tagStateInProgress(clientConfig, target.getAllServers(), tagsInProgress));


            DelegatingLoadBalancer delegating = new DelegatingLoadBalancer(target, tagProcesses == null ? Collections.emptyList() : tagProcesses, tagsInProgress);
            rule.setLoadBalancer(delegating);
            this.tagStateInProgress(clientConfig, target.getAllServers(), tagsInProgress);


            String prefix = clientConfig.getClientName() + ".tag-loadbalancer";
            if (delegatePropChangeListener != null) {
                PropChangeListener listener = new PropChangeListener(prefix, () -> {
                    if (lb instanceof BaseLoadBalancer) {
                        tagStateInProgress(clientConfig,lb.getAllServers(), tagsInProgress);
                    }

                });
                delegatePropChangeListener.addListener(listener);
                prefix = clientConfig.getClientName() + ".tagLoadbalancer";
                listener = new PropChangeListener(prefix, () -> {
                    if (lb instanceof BaseLoadBalancer) {
                        tagStateInProgress(clientConfig,lb.getAllServers(), tagsInProgress);
                    }

                });
                delegatePropChangeListener.addListener(listener);


            }
        }


        return "tagStateProgress";
    }


    protected void tagStateInProgress( IClientConfig clientConfig,List<Server> newList, AtomicReference<Map<Server, String>> tagsInProgress) {

        DefaultClientConfigImpl  defaultClientConfig=(DefaultClientConfigImpl)clientConfig;
        String propName=defaultClientConfig.getInstancePropName(clientConfig.getClientName(),"tag");
        ConfigurableEnvironment enviroment = (ConfigurableEnvironment) env;


        String serverIds=env.getProperty(propName);



     org.apache.commons.configuration.Configuration configuration= ConfigurationManager.getConfigInstance().subset(clientConfig.getClientName());
     configuration.
        IteratorUtils.toList(.getKeys())





        Map<Server, String> tags = tagsInProgress.get();
        Map<Server, String> map = new HashMap<Server, String>(tags);
        String cleintName=clientConfig.getClientName();
//        for (Server server : newList) {
//            if (server.isAlive()) {
//                String key = tagStateInProgressKey(cleintName,server);
//                String tag = env.getProperty(key);
//
//                if (StringUtils.isNotBlank(tag)) {
//                    map.put(server, tag);
//
//                } else {
//                    map.remove(server);
//                }
//
//
//            }
//
//        }
        if (!tags.equals(map)) {
            tagsInProgress.set(map);
        }

    }


}
