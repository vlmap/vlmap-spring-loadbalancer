package com.github.vlmap.spring.tools.loadbalancer.platform.feign;

import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FeignRequestInterceptor implements RequestInterceptor {
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
    public void apply(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        String headerName=properties.getTagHeaderName();

        String header=headers.getOrDefault(headerName,Collections.emptyList()).stream().findFirst().orElse(null);
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
            template.header(headerName,tag);
        }
    }
}
