package com.github.vlmap.cloud.loadbalancer.config;


import com.github.vlmap.cloud.loadbalancer.rule.DelegatingLoadBalancer;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Configuration
public class RibbonClientContextConfiguration extends org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration {
    public static final IClientConfigKey ROUTE_TAG_KEY = CommonClientConfigKey.valueOf("tag");


    @Bean
    @ConditionalOnMissingBean(name = "ribbonLoadBalancer")

    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
        ILoadBalancer lb = super.ribbonLoadBalancer(config, serverList, serverListFilter, rule, ping, serverListUpdater);

        ServerListChangeListener changeListener = new ServerListChangeListener() {
            @Override
            public void serverListChanged(List<Server> oldList, List<Server> newList) {
                config.set(ROUTE_TAG_KEY,"true");
            }
        };
        ServerStatusChangeListener statusChangeListener=new ServerStatusChangeListener(){
            @Override
            public void serverStatusChanged(Collection<Server> servers) {
                config.set(ROUTE_TAG_KEY,"true");
            }
        };


        BaseLoadBalancer baselb = (BaseLoadBalancer) lb;

        baselb.addServerListChangeListener(changeListener);
        baselb.addServerStatusChangeListener(statusChangeListener);
        DelegatingLoadBalancer delegating=   new DelegatingLoadBalancer(lb);
        rule.setLoadBalancer(delegating);

        return delegating;
    }



}
