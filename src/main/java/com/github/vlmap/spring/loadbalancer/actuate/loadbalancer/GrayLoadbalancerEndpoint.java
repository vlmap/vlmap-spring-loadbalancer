package com.github.vlmap.spring.loadbalancer.actuate.loadbalancer;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.attach.AbstractAttachHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.Map;

@Endpoint(id = "gray")
public class GrayLoadbalancerEndpoint {
    @Autowired
    private GrayLoadBalancerProperties properties;
    @Autowired
    private CurrentServer currentServer;
    @Autowired
    private AbstractAttachHandler attachHandler;

    @ReadOperation
    public Map<String, Object> get() {
        Map<String, Object> result = new HashMap<>();
        result.put("properties", properties);
        result.put("currentServer", currentServer);
        result.put("attachParamaters", attachHandler.getAttachParamaters());
        return result;
    }
}
