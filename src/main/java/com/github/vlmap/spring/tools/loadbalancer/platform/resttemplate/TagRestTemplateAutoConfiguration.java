package com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate;

import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Configuration
@AutoConfigureAfter({ RibbonClientSpecificationAutoConfiguration.class, RestTemplateAutoConfiguration.class})
@ConditionalOnProperty(name = "spring.tools.tag-load-balancer.rest-template.enabled",matchIfMissing = true)

public class TagRestTemplateAutoConfiguration {

    @Bean
    public String  doInitTagRestTemplateCustomizer(@Autowired(required = false) RestTemplateBuilder builder,TagRestTemplateInterceptor interceptor){
        if(builder!=null){
            RestTemplateCustomizer customizer = (RestTemplate restTemplate) -> {


                 List<ClientHttpRequestInterceptor> list = new ArrayList<>(restTemplate.getInterceptors());

                if (!list.contains(interceptor)) {
                    list.add(interceptor);
                    restTemplate.setInterceptors(list);
                }


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
        return "doInitTagRestTemplate";
    }


}
