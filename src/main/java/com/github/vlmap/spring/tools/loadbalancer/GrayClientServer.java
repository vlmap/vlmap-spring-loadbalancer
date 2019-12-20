package com.github.vlmap.spring.tools.loadbalancer;

import com.github.vlmap.spring.tools.context.GrayUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.*;

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


    public void listener(EnvironmentChangeEvent event){
        Set<String> keys=event.getKeys();
        if(CollectionUtils.isNotEmpty(keys)){
            String  prefix= StringUtils.upperCase(clientName);
            for(String key:keys){
                if(StringUtils.startsWith(key,prefix)){
                    this.clientServerTags = GrayUtils.tagOfServer(clientName);

                    break;
                }
            }
        }

    }


}
