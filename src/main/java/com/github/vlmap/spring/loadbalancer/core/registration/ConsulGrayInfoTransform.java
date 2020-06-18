package com.github.vlmap.spring.loadbalancer.core.registration;

import org.springframework.cloud.consul.discovery.ConsulServer;

import java.util.Map;

public class ConsulGrayInfoTransform extends AbstractGrayInfoTransform<ConsulServer> {


    @Override
    public Map<String, String> metadata(ConsulServer server) {

        return server.getMetadata();
    }
}
