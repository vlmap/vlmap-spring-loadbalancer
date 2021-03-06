package com.github.vlmap.spring.loadbalancer.core.client.feign;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.util.Platform;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import feign.Request;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.*;

@Aspect

public class GrayFeignClientProxy {
    private GrayLoadBalancerProperties properties;
    private  RequestCreate requestCreate=Platform.isSpringBoot_2()?new DefaultRequestCreate():new OldRequestCreate();
    public GrayFeignClientProxy(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(*   *.*.execute(feign.Request,feign.Request.Options))&&this(feign.Client)")
    public void feignClient() {
    }

    @Around("feignClient()")
    public Object loadBalancerClientAround(ProceedingJoinPoint joinPoint) throws Throwable {


        Object[] args = joinPoint.getArgs();
        Request request = (Request) args[0];
        String header = getGrayHeader(request);
        String tag = header;
        RuntimeContext runtimeContext = ContextManager.getRuntimeContext();

        if (StringUtils.isBlank(tag)) {
            tag = runtimeContext.get(RuntimeContext.REQUEST_TAG_REFERENCE, String.class);

        }
        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            String headerName = properties.getHeaderName();
            Map<String, Collection<String>> headerMap = request.headers();
            if (headerMap == null) {
                headerMap = new LinkedHashMap<>();
            } else {
                headerMap = new LinkedHashMap<>(headerMap);
            }
            headerMap.put(headerName, Collections.unmodifiableCollection(Arrays.asList(tag)));
            request=requestCreate.create(request,headerMap);


            args[0] = request;

        }
        try {

            if (StringUtils.isNotBlank(tag)) {

                runtimeContext.put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);

            }
            return joinPoint.proceed();
        } finally {
            if (Platform.isReactive()) {
                runtimeContext.release();

            }


        }


    }

    protected String getGrayHeader(Request request) {
        String header = null;
        String headerName = this.properties.getHeaderName();

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
        return header;
    }
    interface RequestCreate{
        Request create(Request request,Map<String, Collection<String>> headerMap);
    }
    static class DefaultRequestCreate implements RequestCreate {

        @Override
        public Request create(Request request, Map<String, Collection<String>> headerMap) {
            return  Request.create(request.httpMethod(), request.url(), Collections.unmodifiableMap(headerMap), request.requestBody());
        }
    }

    static class OldRequestCreate implements RequestCreate {

        @Override
        public Request create(Request request, Map<String, Collection<String>> headerMap) {
            return  Request.create(request.method(), request.url(), Collections.unmodifiableMap(headerMap), request.body(), request.charset());
        }
    }

}
