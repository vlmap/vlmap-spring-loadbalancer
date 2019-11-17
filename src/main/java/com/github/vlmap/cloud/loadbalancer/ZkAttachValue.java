package com.github.vlmap.cloud.loadbalancer;

import com.github.vlmap.cloud.loadbalancer.AttachEnvironment;
import com.github.vlmap.cloud.zookeeper.config.ConfigAttachWriter;
import com.github.vlmap.cloud.zookeeper.config.event.AttachRefreshEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.CompositePropertySource;

import javax.annotation.PostConstruct;

public class ZkAttachValue {
    @Autowired
    ConfigAttachWriter writer;
    @Autowired
    EurekaInstanceConfigBean eurekaInstanceConfigBean;

    @Autowired
    CompositePropertySource propertySource;

    AttachEnvironment attachEnvironment;
    @EventListener(AttachRefreshEvent.class)
    public void listener(AttachRefreshEvent event){
        String key=event.getKey();
        if(!StringUtils.startsWith(key,"route."))return;
//        Binder.get(attachEnvironment);

    }
    @PostConstruct
    public void start() {
        if (eurekaInstanceConfigBean != null) {
            String name = StringUtils.lowerCase(eurekaInstanceConfigBean.getAppname());
            String ip = eurekaInstanceConfigBean.getIpAddress();
            int port = eurekaInstanceConfigBean.getNonSecurePort();
            writer.write(CreateMode.PERSISTENT, new String[]{name, ip + ":" + port, "foo"}, "");
        }
    }

}
