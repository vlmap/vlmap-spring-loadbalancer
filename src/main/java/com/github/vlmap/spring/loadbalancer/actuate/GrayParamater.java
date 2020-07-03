package com.github.vlmap.spring.loadbalancer.actuate;

import com.github.vlmap.spring.loadbalancer.core.CurrentInstanceMetadataProvider;
import com.github.vlmap.spring.loadbalancer.core.platform.AttacherFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class GrayParamater {

    @Autowired
    private AttacherFilter attacherFilter;
    @Autowired
    private ResponderFilter responderFilter;
    @Autowired
    private Environment environment;


    @Autowired(required = false)
    private CurrentInstanceMetadataProvider instanceMetadataProvider;

    public Map<String, Object> invoke() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (environment instanceof ConfigurableEnvironment) {
            Map<String, String> map = EnvironmentUtils.getSubset((ConfigurableEnvironment) environment, "vlmap.spring.loadbalancer", true);

            result.put("properties", new TreeMap<>(map));
        }


        result.put("attach", attacherFilter.getParamaters());
        result.put("responder", responderFilter.getParamaters());
        if(instanceMetadataProvider!=null){
            Map<String, String> metadata= instanceMetadataProvider.metadata();

            result.put("metadata",metadata==null? Collections.emptyMap():metadata);
        }
        return result;
    }
}
