package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.event.listener.PropChangeListener;
import com.github.vlmap.spring.tools.loadbalancer.DelegatingLoadBalancer;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//@EnableConfigurationProperties({SpringToolsProperties.class})

@Configuration
 public class TagRibbonClientConfiguration  {

    @Autowired

    private Environment env;

    @Bean
    public AtomicReference<Map<Server, String>> tagsInProgress() {

        return new AtomicReference<Map<Server, String>>(Collections.emptyMap());
    }


    @Bean
    @ConditionalOnBean(TagProcess.class)
    public String tagStateProgress(ILoadBalancer lb, IRule rule,AtomicReference<Map<Server, String>> tagsInProgress, @Autowired(required = false) List<TagProcess> tagProcesses) {


        if (lb instanceof BaseLoadBalancer) {
            BaseLoadBalancer target = (BaseLoadBalancer) lb;
            target.addServerListChangeListener((oldList, newList) -> this.tagStateInProgress(newList, tagsInProgress));
            target.addServerStatusChangeListener(servers -> this.tagStateInProgress(target.getAllServers(), tagsInProgress));


            DelegatingLoadBalancer delegating = new DelegatingLoadBalancer(target, tagProcesses == null ? Collections.emptyList() : tagProcesses, tagsInProgress);
            rule.setLoadBalancer(delegating);
            this.tagStateInProgress(target.getAllServers(), tagsInProgress);
            return String.valueOf(true);
        }
        return String.valueOf(false);

    }

    @Bean
    public PropChangeListener tagloadbalancerListener(IClientConfig clientConfig,ILoadBalancer lb, AtomicReference<Map<Server, String>> tagsInProgress) {


        return new PropChangeListener(clientConfig.getClientName()+".tag-loadbalancer.",()->{
            if (lb instanceof BaseLoadBalancer) {
                tagStateInProgress(lb.getAllServers(), tagsInProgress);
            }

        });


    }


    protected void tagStateInProgress(List<Server> newList, AtomicReference<Map<Server, String>> tagsInProgress) {
        Map<Server, String> tags = tagsInProgress.get();
        Map<Server, String> map = new HashMap<Server, String>(tags);
        for (Server server : newList) {
            if (server.isAlive()) {
                String key = tagStateInProgressKey(server);
                String tag = env.getProperty(key);

                if (StringUtils.isNotBlank(tag)) {
                    map.put(server, tag);

                } else {
                    map.remove(server);
                }


            }

        }
        if (!tags.equals(map)) {
            tagsInProgress.set(map);
        }

    }

    protected String tagStateInProgressKey(Server server) {

        String tagKey = "tag-loadbalancer." + server.getId();

        return env.getProperty(tagKey);

    }

}
