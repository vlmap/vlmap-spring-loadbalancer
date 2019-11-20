package com.github.vlmap.cloud.loadbalancer.rule;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import com.netflix.client.IClientConfigAware;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;


import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

public class DelegatingTagRule implements IRule, IClientConfigAware {

    private  IRule target=new AvailabilityFilteringRule();
    private List<TagProcess> tagProcesses = Collections.emptyList();
    private ILoadBalancer lb;

    public DelegatingTagRule() {

    }

    public DelegatingTagRule(IRule target) {
        this.target = target;
    }
    @PostConstruct
    public void init(){
        System.out.println("xx");
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
            setTag(tag);

        }

        return target.choose(key);
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        this.target.setLoadBalancer(lb);
        this.lb=lb;


    }

    @Override
    public ILoadBalancer getLoadBalancer() {

        return this.lb;
    }


}
