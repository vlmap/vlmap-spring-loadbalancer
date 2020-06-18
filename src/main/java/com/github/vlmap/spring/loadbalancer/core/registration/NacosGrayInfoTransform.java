package com.github.vlmap.spring.loadbalancer.core.registration;

import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;

import java.util.Map;

public class NacosGrayInfoTransform extends AbstractGrayInfoTransform<NacosServer> {


    @Override
    public Map<String, String> metadata(NacosServer server) {

        return server.getMetadata();
    }
}
