package com.github.vlmap.spring.loadbalancer.core.client.resttemplate;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "vlmap.spring.loadbalancer.rest-template.enabled", matchIfMissing = true)

public class GrayRestTemplateConfiguration {

    @Autowired
    public void  initRestTemplate(@Autowired(required = false) RestTemplateBuilder builder,
                                  @Autowired(required = false) List<RestTemplate> templateList,
                                  GrayRestTemplateInterceptor interceptor) {
        RestTemplateCustomizer customizer = (RestTemplate restTemplate) -> {


            List<ClientHttpRequestInterceptor> list = new ArrayList<>();
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

            if (!interceptors.contains(interceptor)) {
                list.add(interceptor);

            }
            list.addAll(interceptors);
            restTemplate.setInterceptors(list);

        };

        if (builder != null) {

            builder.additionalCustomizers(customizer);
        }
        if (CollectionUtils.isNotEmpty(templateList)) {
            for (RestTemplate template : templateList) {
                customizer.customize(template);

            }
        }

    }

    @Bean
    public GrayRestTemplateInterceptor grayClientHttpRequestInterceptor() {
        return new GrayRestTemplateInterceptor();
    }





}
