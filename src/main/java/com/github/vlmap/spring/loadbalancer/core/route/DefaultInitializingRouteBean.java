package com.github.vlmap.spring.loadbalancer.core.route;

import com.github.vlmap.spring.loadbalancer.core.GrayLoadBalancer;
import com.github.vlmap.spring.loadbalancer.core.ServerListMetadataProvider;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;


public class DefaultInitializingRouteBean implements InitializingRouteBean {
    @Autowired
    private ILoadBalancer lb;
    @Autowired
    private ServerListMetadataProvider metadataProvider;
    @Autowired
    private IRule rule;


    @Override
    public void afterPropertiesSet() throws Exception {
        rule.setLoadBalancer(new GrayLoadBalancer(lb, metadataProvider));
    }
}
