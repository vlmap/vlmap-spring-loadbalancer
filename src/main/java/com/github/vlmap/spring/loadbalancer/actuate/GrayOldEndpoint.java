package com.github.vlmap.spring.loadbalancer.actuate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "endpoints.gray")
public class GrayOldEndpoint extends org.springframework.boot.actuate.endpoint.AbstractEndpoint<Map<String, Object>> {


    @Autowired
    private GrayParamater paramater;

    public GrayOldEndpoint() {
        super("gray");

    }

    @Override
    public Map<String, Object> invoke() {

        return paramater.invoke();
    }


}
