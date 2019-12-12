package com.github.vlmap.spring.tools.loadbalancer.client.webclient;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@ConditionalOnProperty(name = "spring.tools.tag-loadbalancer.web-client.enabled", matchIfMissing = true)

public class TagWebClientAutoConfiguration {
    @Bean
    public TagWebClientInterceptor tagWebClientInterceptor() {
        return new TagWebClientInterceptor();
    }

    @Bean
    public WebClientCustomizer tagLoadbalanceClientWebClientCustomizer(
            TagWebClientInterceptor filterFunction) {
        return builder -> builder.filters(new Consumer<List<ExchangeFilterFunction>>() {
            @Override
            public void accept(List<ExchangeFilterFunction> exchangeFilterFunctions) {
                List<ExchangeFilterFunction> list = new ArrayList<>();
                if (!exchangeFilterFunctions.contains(filterFunction)) {
                    list.add(filterFunction);
                }
                list.addAll(exchangeFilterFunctions);
                exchangeFilterFunctions.clear();
                exchangeFilterFunctions.addAll(list);
            }
        });
    }


}
