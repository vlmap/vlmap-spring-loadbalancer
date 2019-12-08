package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.context.ContextManager;
import com.github.vlmap.spring.tools.loadbalancer.context.RuntimeContext;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.server.ServerWebExchange;

@Aspect
public class TagLoadBalancerClientFilterProxy {
    private SpringToolsProperties properties;

    public TagLoadBalancerClientFilterProxy(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(*  org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.filter(org.springframework.web.server.ServerWebExchange,org.springframework.cloud.gateway.filter.GatewayFilterChain))")
    public void loadBalancerClientFilter() {
    }

    @Around("loadBalancerClientFilter()")
    public Object loadBalancerClientFilterAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {

            Object[] args = joinPoint.getArgs();
            ServerWebExchange exchange = (ServerWebExchange) args[0];
            String headerName = this.properties.getTagHeaderName();
            String header = null;
            if (exchange != null) {
                header = exchange.getRequest().getHeaders().getFirst(headerName);
            }
            if (StringUtils.isBlank(header)) {
                header = properties.getTagLoadbalancer().getHeader();
            }
            exchange.getAttributes().put(RuntimeContext.REQUEST_TAG_REFERENCE, header);
            ContextManager.getRuntimeContext().setTag(header);

            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }


    }


}
