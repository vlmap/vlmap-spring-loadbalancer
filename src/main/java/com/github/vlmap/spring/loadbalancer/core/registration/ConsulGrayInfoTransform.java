package com.github.vlmap.spring.loadbalancer.core.registration;

import com.netflix.client.config.IClientConfig;
import org.springframework.cloud.consul.discovery.ConsulServer;

import java.util.Map;

public class ConsulGrayInfoTransform extends AbstractGrayInfoTransform<ConsulServer> {
    public ConsulGrayInfoTransform(IClientConfig config) {
        super(config);
    }

    @Override
    public Map<String, String> metadata(ConsulServer server) {

        return server.getMetadata();
    }
}
