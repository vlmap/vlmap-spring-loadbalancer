package com.github.vlmap.spring.tools.loadbalancer.platform.reactive;

import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import com.github.vlmap.spring.tools.context.ContextManager;
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
        String headerName = this.properties.getHeaderName();
        String tag = null;
        if (exchange != null) {
            tag = exchange.getRequest().getHeaders().getFirst(headerName);
        }

        try {

            ContextManager.getRuntimeContext().setTag(tag);

            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }





    }


}
