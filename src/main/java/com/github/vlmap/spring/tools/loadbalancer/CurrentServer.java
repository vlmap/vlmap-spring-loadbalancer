package com.github.vlmap.spring.tools.loadbalancer;

import com.netflix.appinfo.InstanceInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import javax.annotation.PostConstruct;
import java.util.Set;


public class CurrentServer extends GrayClientServer {
    private String id;
    private Set<String> currentGrayServerTags;
    public CurrentServer(InstanceInfo instanceInfo) {
        super(instanceInfo.getAppName());
        this.id = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();

    }

    @Override
    @PostConstruct
    public void initMethod() {
        super.initMethod();
        if(clientServerTags!=null){
            currentGrayServerTags =clientServerTags.get(id)    ;
        }

    }
    public boolean isGrayServer(){
        return CollectionUtils.isNotEmpty(currentGrayServerTags);
    }
    public boolean container( String tag){
        if(clientServerTags!=null){
            return  currentGrayServerTags.contains(tag);
        }
        return false;
    }


}
