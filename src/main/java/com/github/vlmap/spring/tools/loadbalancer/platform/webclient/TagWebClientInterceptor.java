package com.github.vlmap.spring.tools.loadbalancer.platform.webclient;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

public class TagWebClientInterceptor implements ExchangeFilterFunction {
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
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {

        HttpHeaders headers = request.headers();
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

        return next.exchange(request);
    }
}
