package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.util.GrayUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

public class GrayClientServer {
    private String clientName;
    Map<String, Set<String>> clientServerTags;

    public Map<String, Set<String>> getClientServerTags() {
        return clientServerTags;
    }

    public GrayClientServer(String clientName) {
        this.clientName = clientName;
    }

    @PostConstruct
    public void initMethod() {
        this.clientServerTags = GrayUtils.tagOfServer(clientName);
    }


    public void listener(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        if (CollectionUtils.isNotEmpty(keys)) {
            String prefix = StringUtils.upperCase(clientName);
            for (String key : keys) {
                if (StringUtils.startsWith(key, prefix)) {
                    this.clientServerTags = GrayUtils.tagOfServer(clientName);

                    break;
                }
            }
        }

    }


}
