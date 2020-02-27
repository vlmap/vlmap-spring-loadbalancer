package com.github.vlmap.spring.loadbalancer.actuate.loadbalancer;

import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.platform.AttacherFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Endpoint(id = "gray")
public class GrayRouteEndpoint {

    @Autowired
    private CurrentServer currentServer;
    @Autowired
    private AttacherFilter attacherFilter;
    @Autowired
    private ResponderFilter responderFilter;
    @Autowired
    private Environment environment;

    @ReadOperation
    public Map<String, Object> get() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (environment instanceof ConfigurableEnvironment) {
            MapConfigurationPropertySource propertySource = EnvironmentUtils.getSubsetConfigurationPropertySource((ConfigurableEnvironment) environment, "vlmap.spring.loadbalancer", true);

            Map<String, Object> source = (Map<String, Object>) propertySource.getUnderlyingSource();
            result.put("properties", new TreeMap<>(source));
        }

        result.put("currentServer", currentServer);
        result.put("attach", attacherFilter.getParamaters());
        result.put("responder", responderFilter.getParamaters());
        return result;
    }
}
