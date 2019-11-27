package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.DelegatingLoadBalancer;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@EnableConfigurationProperties({SpringToolsProperties.class})

@Configuration
 public class TagRibbonClientConfiguration extends org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration {
    @Autowired

    private DynamicToolProperties properties;
    @Autowired

    Environment env;





    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String tagStateProgress(IClientConfig clientConfig, ILoadBalancer lb, IRule rule, @Autowired(required = false) List<TagProcess> tagProcesses) {


        if (lb instanceof BaseLoadBalancer) {
            AtomicBoolean tagStateInProgress = new AtomicBoolean(false);
            AtomicReference<Map<Server, String>> tagsInProgress = new AtomicReference<Map<Server, String>>(Collections.emptyMap());
            BaseLoadBalancer target = (BaseLoadBalancer) lb;
            Map propertySource = getSource();
            target.addServerListChangeListener((oldList, newList) -> this.tagStateInProgress(clientConfig, propertySource, newList, tagStateInProgress, tagsInProgress));
            target.addServerStatusChangeListener(servers -> this.tagStateInProgress(clientConfig, propertySource, target.getAllServers(), tagStateInProgress, tagsInProgress));
            DelegatingLoadBalancer delegating = new DelegatingLoadBalancer(target, tagProcesses == null ? Collections.emptyList() : tagProcesses, tagStateInProgress, tagsInProgress);
            rule.setLoadBalancer(delegating);
            this.tagStateInProgress(clientConfig, propertySource, target.getAllServers(), tagStateInProgress, tagsInProgress);
            return String.valueOf(true);
        }
        return String.valueOf(false);

    }

    protected Map getSource() {
        PropertySource propertySource = properties.getDefaultToolsProps();
        if (propertySource != null) {
            Object source = propertySource.getSource();
            if (source instanceof Map) {
                return (Map) source;
            }
        }

        return null;
    }

    protected void tagStateInProgress(IClientConfig clientConfig, Map source, List<Server> newList, AtomicBoolean tagRuleInProgress, AtomicReference<Map<Server, String>> tagsInProgress) {
        Map<Server, String> tags = tagsInProgress.get();
        Map<Server, String> map = new HashMap<Server, String>(tags);
        for (Server server : newList) {
            if (server.isAlive()) {
                String tag = tagStateInProgress(clientConfig, source, server);

                if (StringUtils.isNotBlank(tag)) {
                    map.put(server, tag);
                    tagRuleInProgress.set(true);
                    if (!tags.equals(map)) {
                        tagsInProgress.set(map);
                    }
                    return;
                } else {
                    map.remove(server);
                }


            }

        }

        tagRuleInProgress.set(false);
        tagsInProgress.set(Collections.emptyMap());
    }

    protected String tagStateInProgress(IClientConfig clientConfig, Map source, Server server) {

        String name = clientConfig.getClientName();
        String tagKey = "tag-loadbalancer." + name + ".process." + server.getId();
        String tagValue = null;
        if (source != null) {
            tagValue = (String) source.get(tagKey);
            if (tagValue == null) {
                tagValue = env.getProperty(tagKey);
                source.put(tagKey, tagValue == null ? "" : tagValue);
            }
        } else {
            tagValue = env.getProperty(tagKey);
        }

        return tagValue;

    }

}
