package com.github.vlmap.cloud.loadbalancer.ribbon;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import com.netflix.client.IClientConfigAware;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class DelegatingTagRule implements IRule, IClientConfigAware {
    private  IRule target;

    public DelegatingTagRule(IRule target) {
        this.target = target;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        if(target instanceof IClientConfigAware){
            ((IClientConfigAware)target).initWithNiwsConfig(clientConfig);
        }
    }
    @Autowired
    private Map<String, TagProcess> tagProcessMap;

    protected String tag() {
        for (TagProcess process : tagProcessMap.values()) {
            String tag = process.getTag();
            if (StringUtils.isNotBlank(tag)) {
                return tag;
            }
        }
        return null;
    }

    protected void setTag(String tag) {
        for (TagProcess process : tagProcessMap.values()) {
            process.setTag(tag);

        }

    }

    protected String getServerTag(Server server) {
        return null;
    }
    @Override
    public Server choose(Object key) {
        String tag = tag();
        if (StringUtils.isNotBlank(tag)) {
            setTag(tag);

        }
            return target.choose(key);
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        target.setLoadBalancer(lb);
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return target.getLoadBalancer();
    }


}
