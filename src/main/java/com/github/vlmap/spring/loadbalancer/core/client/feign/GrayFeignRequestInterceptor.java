package com.github.vlmap.spring.loadbalancer.core.client.feign;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GrayFeignRequestInterceptor implements RequestInterceptor {

    @Autowired

    private GrayLoadBalancerProperties properties;


    @Override
    public void apply(RequestTemplate template) {

        Map<String, Collection<String>> headers = template.headers();
        String headerName = properties.getHeaderName();

        String header = headers.getOrDefault(headerName, Collections.emptyList()).stream().findFirst().orElse(null);
        String tag = header;
        if (StringUtils.isBlank(tag)) {
            tag = ContextManager.getRuntimeContext().getTag();

        }

        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            template.header(headerName, tag);
        }
    }
}
