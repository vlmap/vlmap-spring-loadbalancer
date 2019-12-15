package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.context.ContextManager;
import com.github.vlmap.spring.tools.context.RuntimeContext;
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

    private SpringToolsProperties properties;

    public GrayLoadBalancerClientFilterProxy(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(*  org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.filter(org.springframework.web.server.ServerWebExchange,org.springframework.cloud.gateway.filter.GatewayFilterChain))")
    public void loadBalancerClientFilter() {
    }

    @Around("loadBalancerClientFilter()")
    public Object loadBalancerClientFilterAround(ProceedingJoinPoint joinPoint) throws Throwable {


        Object[] args = joinPoint.getArgs();
        ServerWebExchange exchange = (ServerWebExchange) args[0];
        String headerName = this.properties.getTagHeaderName();
        String tag = null;
        if (exchange != null) {
            tag = exchange.getRequest().getHeaders().getFirst(headerName);
        }


        try {

            if (StringUtils.isBlank(tag)) {
                tag = properties.getGrayLoadbalancer().getHeader();
            }
            exchange.getAttributes().put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
            ContextManager.getRuntimeContext().setTag(tag);

            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }


    }


}
