package com.github.vlmap.cloud.loadbalancer.rule;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import com.netflix.client.IClientConfigAware;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DelegatingTagRule implements IRule, IClientConfigAware {
    private  IRule target;
    private String currentServerTag;
    private List<TagProcess> tagProcesses= Collections.emptyList();
    public DelegatingTagRule(IRule target,String currentServerTag) {
        this.target = target;
        this.currentServerTag=currentServerTag;
    }

    public void setTagProcesses(List<TagProcess> tagProcesses) {
        this.tagProcesses = tagProcesses;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        if(target instanceof IClientConfigAware){
            ((IClientConfigAware)target).initWithNiwsConfig(clientConfig);
        }
    }


    protected String tag() {
        for (TagProcess process : tagProcesses) {
            String tag = process.getTag();
            if (StringUtils.isNotBlank(tag)) {
                return tag;
            }
        }
        return null;
    }

    protected void setTag(String tag) {
        for (TagProcess process : tagProcesses) {
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
            if(StringUtils.isNotBlank(currentServerTag)){
                setTag(currentServerTag);
            }


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
