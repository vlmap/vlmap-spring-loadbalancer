package com.github.vlmap.spring.tools.loadbalancer.client.feign;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.context.ContextManager;
import feign.Request;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Collection;
import java.util.Map;

@Aspect
public class GrayFeignClientProxy {
    private SpringToolsProperties properties;

    public GrayFeignClientProxy(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(*   *.*.execute(feign.Request,feign.Request.Options))&&this(feign.Client)")
    public void feignClient() {
    }

    @Around("feignClient()")
    public Object loadBalancerClientAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {

            Object[] args = joinPoint.getArgs();
            Request request = (Request) args[0];
            String header = null;
            String headerName = this.properties.getTagHeaderName();
            Map<String, Collection<String>> headers = request.headers();
            if (headers != null) {
                Collection<String> collection = headers.get(headerName);
                if (collection != null) {
                    for (String value : collection) {
                        header = value;
                        break;
                    }

                }
            }


            if (StringUtils.isBlank(header)) {
                header = properties.getGrayLoadbalancer().getHeader();
            }

            ContextManager.getRuntimeContext().setTag(header);

            return joinPoint.proceed();


        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }


    }


}
