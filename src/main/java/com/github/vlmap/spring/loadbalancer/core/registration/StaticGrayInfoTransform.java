package com.github.vlmap.spring.loadbalancer.core.registration;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;

import java.util.Map;

public class StaticGrayInfoTransform extends AbstractGrayInfoTransform<Server> {
    private IClientConfig config = null;

    public StaticGrayInfoTransform(IClientConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, String> metadata(Server server) {
        return null;
    }
}
