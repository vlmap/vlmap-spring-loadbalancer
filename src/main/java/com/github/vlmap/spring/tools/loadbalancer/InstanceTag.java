package com.github.vlmap.spring.tools.loadbalancer;

import com.github.vlmap.spring.tools.loadbalancer.GrayClientServer;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RefreshScope
public class InstanceTag {
    ApplicationInfoManager applicationInfoManager;

    public InstanceTag(ApplicationInfoManager applicationInfoManager) {
        this.applicationInfoManager = applicationInfoManager;
    }

    Set<String> tags = null;

    @PostConstruct
    public void initMethod() {
        InstanceInfo instanceInfo = applicationInfoManager.getInfo();
        String appName = instanceInfo.getAppName();
        String id = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
        GrayClientServer result = new GrayClientServer(appName);
        result.initMethod();
        Map<String, Set<String>> clientServerTags = result.getClientServerTags();
        tags = clientServerTags.get(id);
    }

    public Collection<String> get() {

        return tags;

    }


}
