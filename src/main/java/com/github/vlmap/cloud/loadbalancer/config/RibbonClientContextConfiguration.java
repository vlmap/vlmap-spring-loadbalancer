package com.github.vlmap.cloud.loadbalancer.config;


import com.github.vlmap.cloud.loadbalancer.rule.DelegatingLoadBalancer;
import com.github.vlmap.cloud.loadbalancer.tag.ReactorTagProcess;
import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@EnableConfigurationProperties({SpringToolsProperties.class})

@Configuration
public class RibbonClientContextConfiguration extends org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration {
    @Autowired
    private SpringToolsProperties properties;
    @Autowired
    Environment env;
    @Bean
    public TagProcess reactorTagProcess() {
        return new ReactorTagProcess();

    }
    @Bean
    public String tagStateProgress(IClientConfig clientConfig, ILoadBalancer lb, IRule rule, List<TagProcess> tagProcesses) {


        if (lb instanceof BaseLoadBalancer) {
            AtomicBoolean tagStateInProgress = new AtomicBoolean(false);
            AtomicReference<Map<Server,String>> tagsInProgress=new AtomicReference<Map<Server, String>>(Collections.emptyMap());
            BaseLoadBalancer target = (BaseLoadBalancer) lb;
            MapPropertySource propertySource = getPropertySource();
            target.addServerListChangeListener((oldList, newList) -> this.tagStateInProgress(clientConfig,propertySource, newList, tagStateInProgress,tagsInProgress));
            target.addServerStatusChangeListener(servers -> this.tagStateInProgress(clientConfig,propertySource, target.getAllServers(), tagStateInProgress,tagsInProgress));
            DelegatingLoadBalancer delegating = new DelegatingLoadBalancer(target, tagStateInProgress,tagsInProgress);
            rule.setLoadBalancer(delegating);
            this.tagStateInProgress(clientConfig,propertySource, target.getAllServers(), tagStateInProgress,tagsInProgress);
            return String.valueOf(true);
        }
        return String.valueOf(false);

    }

    protected MapPropertySource getPropertySource( ) {

        if (ConfigurableEnvironment.class.isInstance(env)) {
            ConfigurableEnvironment environment = (ConfigurableEnvironment) env;
            PropertySource propertySource= environment.getPropertySources().get(properties.getPropertySource());
            if(propertySource instanceof MapPropertySource){
                return (MapPropertySource)propertySource;
            }
        }
        return null;
    }

    protected void tagStateInProgress(IClientConfig clientConfig,MapPropertySource propertySource, List<Server> newList, AtomicBoolean tagRuleInProgress,AtomicReference<Map<Server,String>> tagsInProgress) {
        Map<Server,String> tags=tagsInProgress.get();
        Map<Server,String> map=  new HashMap<Server,String>(tags);
        for (Server server : newList) {
            if (server.isAlive()) {
                String tag=tagStateInProgress(clientConfig,propertySource,server);

                if(StringUtils.isNotBlank(tag)){
                    map.put(server,tag);
                    tagRuleInProgress.set(true);
                    if(!tags.equals(map)){
                        tagsInProgress.set(map);
                    }
                    return;
                }else{
                    map.remove(server);
                }


            }

        }

        tagRuleInProgress.set(false);
        tagsInProgress.set(Collections.emptyMap());
    }

    protected String  tagStateInProgress(IClientConfig clientConfig,MapPropertySource propertySource, Server server) {

        String name=clientConfig.getClientName();
        String tagKey="spring.tools.loadbalancer."+name+".tag."+server.getId();
        String tagValue=null;
        if(propertySource!=null){
            tagValue=  (String)propertySource.getProperty(tagKey);
            if(tagValue==null){
                tagValue=   env.getProperty(tagKey);
                propertySource.getSource().put(tagKey,tagValue==null?"":tagValue);
            }
        }else{
            tagValue=   env.getProperty(tagKey);
        }

        return tagValue;

    }

}
