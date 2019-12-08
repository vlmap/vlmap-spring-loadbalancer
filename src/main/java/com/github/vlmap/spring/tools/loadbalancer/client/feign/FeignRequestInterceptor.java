package com.github.vlmap.spring.tools.loadbalancer.client.feign;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.context.ContextManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class FeignRequestInterceptor implements RequestInterceptor {

    @Autowired

    private SpringToolsProperties properties;



    @Override
    public void apply(RequestTemplate template) {

        Map<String, Collection<String>> headers = template.headers();
        String headerName = properties.getTagHeaderName();

        String header = headers.getOrDefault(headerName, Collections.emptyList()).stream().findFirst().orElse(null);
        String tag = header;
        if (StringUtils.isBlank(tag)) {
            tag=ContextManager.getRuntimeContext().getTag();

        }
        if (StringUtils.isBlank(tag)) {
            tag=properties.getTagLoadbalancer().getHeader();

        }
        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            template.header(headerName, tag);
        }
    }
}
