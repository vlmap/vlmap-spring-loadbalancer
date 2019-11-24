package com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Configuration

public class TagRestTemplateAutoConfiguration {
    @Bean
    public TagRestTemplateInterceptor tagClientHttpRequestInterceptor() {
        return new TagRestTemplateInterceptor();
    }



    @Bean
    public String doTagRestTemplateCustomizer(@Autowired(required = false) List<RestTemplate> templateList,
                                              TagRestTemplateInterceptor interceptor) {
        RestTemplateCustomizer customizer = (RestTemplate restTemplate) -> {


            if (interceptor == null) return;
            List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());

            if (!list.contains(interceptor)) {
                list.add(interceptor);
                restTemplate.setInterceptors(list);
            }


        };
        if (CollectionUtils.isNotEmpty(templateList)) {
            for (RestTemplate template : templateList) {
                customizer.customize(template);

            }
        }
        return "tagRestTemplateCustomizer";
    }


}
