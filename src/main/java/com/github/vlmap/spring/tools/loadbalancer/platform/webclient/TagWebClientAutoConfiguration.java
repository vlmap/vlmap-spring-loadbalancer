package com.github.vlmap.spring.tools.loadbalancer.platform.webclient;



import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(name = "spring.tools.tag-load-balancer.web-client.enabled",matchIfMissing = true)

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
