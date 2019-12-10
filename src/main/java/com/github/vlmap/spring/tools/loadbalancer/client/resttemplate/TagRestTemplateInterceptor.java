package com.github.vlmap.spring.tools.loadbalancer.client.resttemplate;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.context.ContextManager;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
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
@ConditionalOnProperty(value = "spring.tools.tag-loadbalancer.rest-template.enabled", matchIfMissing = true)

public class TagRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Autowired

    private SpringToolsProperties properties;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String headerName = properties.getTagHeaderName();
        String header = headers.getFirst(headerName);
        String tag = header;
        if (StringUtils.isBlank(tag)) {
            tag = ContextManager.getRuntimeContext().getTag();

        }
        if (StringUtils.isBlank(tag)) {
            tag = properties.getTagLoadbalancer().getHeader();

        }
        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            headers.add(headerName, tag);
        }
        if (Platform.getInstnce().isReactive()) {
            try {
                ContextManager.getRuntimeContext().setTag(tag);
                return execution.execute(request, body);
            } finally {
                ContextManager.getRuntimeContext().onComplete();
            }
        } else {
            return execution.execute(request, body);
        }

    }
}
