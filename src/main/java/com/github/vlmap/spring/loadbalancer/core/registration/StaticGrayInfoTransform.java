package com.github.vlmap.spring.loadbalancer.core.registration;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.Server;

import java.util.Map;

public class StaticGrayInfoTransform extends AbstractGrayInfoTransform<Server> {
    private IClientConfig config = null;

    public StaticGrayInfoTransform(IClientConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, String> metadata(Server server) {

        if(config!=null){
//            IClientConfig key=    CommonClientConfigKey.valueOf(server.getId()+"")
//            config.getPropertyAsString(IClientConfig)
//            server.getId()
        }
        return null;
    }
}
