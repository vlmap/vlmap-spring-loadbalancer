package com.github.vlmap.spring.tools.loadbalancer.client.resttemplate;

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
@ConditionalOnProperty(name = "spring.tools.tag-loadbalancer.rest-template.enabled", matchIfMissing = true)

public class TagRestTemplateAutoConfiguration {

    @Bean
    public String doInitTagRestTemplateCustomizer(@Autowired(required = false) RestTemplateBuilder builder, TagRestTemplateInterceptor interceptor) {
        if (builder != null) {
            RestTemplateCustomizer customizer = (RestTemplate restTemplate) -> {


                List<ClientHttpRequestInterceptor> list = new ArrayList<>();
                List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

                if (!interceptors.contains(interceptor)) {
                    list.add(interceptor);

                }
                list.addAll(interceptors);
                restTemplate.setInterceptors(list);

            };
            builder.additionalCustomizers(customizer);
        }
        return "doInitTagRestTemplateCustomizer";
    }

    @Bean
    public TagRestTemplateInterceptor tagClientHttpRequestInterceptor() {
        return new TagRestTemplateInterceptor();
    }


    @Bean
    public String doInitTagRestTemplate(@Autowired(required = false) List<RestTemplate> templateList,
                                        TagRestTemplateInterceptor interceptor) {
        RestTemplateCustomizer customizer = (RestTemplate restTemplate) -> {


//

            List<ClientHttpRequestInterceptor> list = new ArrayList<>();
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

            if (!interceptors.contains(interceptor)) {
                list.add(interceptor);

            }
            list.addAll(interceptors);
            restTemplate.setInterceptors(list);

        };
        if (CollectionUtils.isNotEmpty(templateList)) {
            for (RestTemplate template : templateList) {
                customizer.customize(template);

            }
        }
        return "doInitTagRestTemplate";
    }


}
