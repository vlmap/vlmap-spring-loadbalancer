package com.github.vlmap.spring.loadbalancer.core.registration;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class StaticGrayInfoTransform extends AbstractGrayInfoTransform<Server> {
    public StaticGrayInfoTransform(IClientConfig config) {
        super(config);
    }

    @Override
    public Map<String, String> metadata(Server server) {

        if (config != null) {
            Map<String, Object> properties = config.getProperties();
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                result.put(entry.getKey(), ObjectUtils.toString(entry.getValue()));
            }
            return result;

        }
        return null;
    }
}
