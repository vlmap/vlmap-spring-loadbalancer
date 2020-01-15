package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;

@Aspect
public class GrayLoadBalancerClientFilterProxy {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;

    public GrayLoadBalancerClientFilterProxy(GrayLoadBalancerProperties properties) {
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


        try {
            ContextManager.getRuntimeContext().put(RuntimeContext.REACTIVE_SERVER_WEB_EXCHANGE, exchange);
            if(StringUtils.isNotBlank(tag)){
                 ContextManager.getRuntimeContext().put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
            }


            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }


    }


}
