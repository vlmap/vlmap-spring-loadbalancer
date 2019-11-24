package com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate;

import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty( "spring.tools.tag-load-balancer.rest-template.enabled")

public class TagRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Autowired(required = false)
    List<TagProcess> tagProcesses= Collections.emptyList();
    @Autowired

    private DynamicToolProperties properties;
    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(tagProcesses)) {
            AnnotationAwareOrderComparator.sort(tagProcesses);

        }
    }
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String headerName=properties.getTagHeaderName();
        String header=headers.getFirst(headerName);
        String tag=header;
        if(StringUtils.isBlank(tag)) {
            for (TagProcess tagProcess : tagProcesses) {
                String _tag = tagProcess.getTag();
                if (StringUtils.isNotBlank(_tag)) {
                    tag = _tag;
                    break;
                }
            }
        }
        if(StringUtils.isNotBlank(tag)&&!StringUtils.equals(tag,header)){
            headers.add(headerName,tag);
        }

        return execution.execute(request, body);
    }
}
