package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.server.ServerWebExchange;

@Aspect
public class LoadBalancerClientFilterProxy {

    private GrayLoadBalancerProperties properties;

    public LoadBalancerClientFilterProxy(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(*  org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.filter(org.springframework.web.server.ServerWebExchange,org.springframework.cloud.gateway.filter.GatewayFilterChain))")
    public void loadBalancerClientFilter() {
    }

    @Around("loadBalancerClientFilter()")
    public Object loadBalancerClientFilterAround(ProceedingJoinPoint joinPoint) throws Throwable {


        Object[] args = joinPoint.getArgs();
        ServerWebExchange exchange = (ServerWebExchange) args[0];

        String headerName = properties.getHeaderName();

        String tag = exchange.getRequest().getHeaders().getFirst(headerName);

        RuntimeContext runtimeContext = null;

        if (StringUtils.isNotBlank(tag)) {
            runtimeContext = ContextManager.getRuntimeContext();
            runtimeContext.put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
        }

        try {
            return joinPoint.proceed();

        } finally {
            if (runtimeContext != null) {
                runtimeContext.release();

            }
        }


    }


}
