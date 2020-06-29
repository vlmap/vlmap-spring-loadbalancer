package com.github.vlmap.spring.loadbalancer.core.client.resttemplate;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "vlmap.spring.loadbalancer.rest-template.enabled", matchIfMissing = true)
@EnableConfigurationProperties(GrayLoadBalancerProperties.class)

 public class GrayRestTemplateConfiguration {


    @Configuration
    @ConditionalOnClass(RestTemplateCustomizer.class)

    public static class SpringBoot2_RestTemplateCustomizerConfiguration {

        @Bean
        public RestTemplateCustomizer restTemplateCustomizer(GrayRestTemplateInterceptor interceptor) {
            RestTemplateCustomizer customizer = (restTemplate) -> {


                List<ClientHttpRequestInterceptor> list = new ArrayList<>();
                List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

                if (!interceptors.contains(interceptor)) {
                    list.add(interceptor);

                }
                list.addAll(interceptors);
                restTemplate.setInterceptors(list);

            };
            return customizer;
        }

    }

    @Configuration
    @ConditionalOnClass(org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer.class)

    public static class SpringBoot1_RestTemplateCustomizerConfiguration {

        @Bean
        public org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer restTemplateCustomizer(GrayRestTemplateInterceptor interceptor) {
            org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer customizer = (restTemplate) -> {


                List<ClientHttpRequestInterceptor> list = new ArrayList<>();
                List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

                if (!interceptors.contains(interceptor)) {
                    list.add(interceptor);

                }
                list.addAll(interceptors);
                restTemplate.setInterceptors(list);

            };
            return customizer;
        }

    }

    @Bean
    public GrayRestTemplateInterceptor grayClientHttpRequestInterceptor() {
        return new GrayRestTemplateInterceptor();
    }


}
