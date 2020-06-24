package com.github.vlmap.spring.loadbalancer.actuate;


import com.github.vlmap.spring.loadbalancer.core.platform.AttacherFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Endpoint(id = "gray")
public class GrayRouteEndpoint  {



    @Autowired
    private AttacherFilter attacherFilter;
    @Autowired
    private ResponderFilter responderFilter;
    @Autowired
    private Environment environment;

    @ReadOperation
    public  Map<String, Object> get() {
        Map<java.lang.String, java.lang.Object> result = new LinkedHashMap<>();
        if (environment instanceof ConfigurableEnvironment) {
            Map<java.lang.String, java.lang.String> map = EnvironmentUtils.getSubset((ConfigurableEnvironment) environment, "vlmap.spring.loadbalancer", true);

            result.put("properties", new TreeMap<>(map));
        }


        result.put("attach", attacherFilter.getParamaters());
        result.put("responder", responderFilter.getParamaters());
        return result;
    }
}
