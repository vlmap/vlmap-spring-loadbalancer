package com.github.vlmap.cloud.loadbalancer;

import com.github.vlmap.cloud.zookeeper.config.event.AttachRefreshEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class TagRuleMatch {

    @Autowired
    private PropertySource propertySource;



    boolean hasTag=false;

    protected void initData(){
        propertySource.getProperty("route.tag.name");

    }

    @EventListener(AttachRefreshEvent.class)
    public void listener(AttachRefreshEvent event){

    }
}
