package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.GrayInvocableHandlerMethod;
import org.springframework.web.reactive.result.method.InvocableHandlerMethod;

public class RequestMappingInvoker {

    private GrayLoadBalancerProperties properties;

    public RequestMappingInvoker(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    public InvocableHandlerMethod invocableMethod(ReactiveAdapterRegistry reactiveAdapterRegistry,InvocableHandlerMethod invocable, HandlerMethod handlerMethod) {
        GrayInvocableHandlerMethod result = new GrayInvocableHandlerMethod(handlerMethod);
        result.setProperties(this.properties);
        result.setArgumentResolvers(invocable.getResolvers());
        result.setReactiveAdapterRegistry(reactiveAdapterRegistry);
        return result;
    }
}
