package com.github.vlmap.spring.loadbalancer.actuate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

@Endpoint(id = "gray")
public class GrayEndpoint {


    @Autowired
    private GrayParamater paramater;


    @ReadOperation
    public Map<String, Object> get() {

        return paramater.invoke();
    }
}
