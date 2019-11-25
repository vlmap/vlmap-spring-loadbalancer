package com.github.vlmap.spring.tools.loadbalancer.platform.webclient;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
@ConditionalOnClass(WebClient.class)
@ConditionalOnBean(LoadBalancerClient.class)

public class TagWebClientAutoConfiguration {
    @Bean
    public TagWebClientInterceptor tagWebClientInterceptor() {
        return new TagWebClientInterceptor();
    }

    @Bean
    public WebClientCustomizer tagLoadbalanceClientWebClientCustomizer(
            TagWebClientInterceptor filterFunction) {
        return builder -> builder.filter(filterFunction);
    }




}
