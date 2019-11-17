package com.github.vlmap.cloud.zookeeper.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

import javax.annotation.PostConstruct;

public class ZkAttachValue {
    @Autowired
    ConfigAttachWriter writer;
    @Autowired
    EurekaInstanceConfigBean eurekaInstanceConfigBean;

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
