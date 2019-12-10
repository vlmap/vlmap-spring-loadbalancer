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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@Aspect
public class TagLoadBalancerClientFilterProxy {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private SpringToolsProperties properties;

    public TagLoadBalancerClientFilterProxy(SpringToolsProperties properties) {
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

        String serverTag = properties.getTagLoadbalancer().getHeader();
        /**
         * 非兼容模式,请求标签不匹配拒绝响应
         */
        SpringToolsProperties.Compatible compatible = properties.getCompatible();
        if (!compatible.isEnabled() && org.apache.commons.lang3.StringUtils.isNotBlank(serverTag) && !org.apache.commons.lang3.StringUtils.equals(tag, serverTag)) {
            if (logger.isInfoEnabled()) {
                logger.info("The server isn't compatible model,current request Header[" + headerName + ":" + tag + "] don't match \"" + serverTag + "\",response code:" + compatible.getCode());

            }
            String message = compatible.getMessage();
            HttpStatus status = HttpStatus.valueOf(compatible.getCode());
            if (org.apache.commons.lang3.StringUtils.isBlank(message)) {
                throw new ResponseStatusException(status);

            } else {
                throw new ResponseStatusException(status, message);
            }

        }

        try {

            if (StringUtils.isBlank(tag)) {
                tag = properties.getTagLoadbalancer().getHeader();
            }
            exchange.getAttributes().put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
            ContextManager.getRuntimeContext().setTag(tag);

            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }


    }


}
