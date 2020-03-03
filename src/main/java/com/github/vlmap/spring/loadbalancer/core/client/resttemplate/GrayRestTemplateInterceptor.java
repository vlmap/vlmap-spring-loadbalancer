package com.github.vlmap.spring.loadbalancer.core.client.resttemplate;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(value = "vlmap.spring.loadbalancer.rest-template.enabled", matchIfMissing = true)

public class GrayRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Autowired

    private GrayLoadBalancerProperties properties;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String headerName = properties.getHeaderName();
        String header = headers.getFirst(headerName);
        String tag = header;
        RuntimeContext runtimeContext = ContextManager.getRuntimeContext();
        if (StringUtils.isBlank(tag)) {
            tag = runtimeContext.get(RuntimeContext.REQUEST_TAG_REFERENCE, String.class);

        }

        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            headers.add(headerName, tag);
        }
        try {
            if (StringUtils.isNotBlank(tag)) {
                runtimeContext.put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);

            }
            return execution.execute(request, body);
        } finally {
            if (Platform.getInstnce().isReactive()) {
                runtimeContext.release();

            }
        }


    }
}
