package com.github.vlmap.spring.loadbalancer.core.registration;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.Map;

public class EurekaGrayInfoTransform extends AbstractGrayInfoTransform<DiscoveryEnabledServer> {
    public EurekaGrayInfoTransform(IClientConfig config) {
        super(config);
    }

    @Override
    public Map<String, String> metadata(DiscoveryEnabledServer server) {
        InstanceInfo instanceInfo = server.getInstanceInfo();
        if (instanceInfo != null) {
            return instanceInfo.getMetadata();
        }
        return null;
    }
}
