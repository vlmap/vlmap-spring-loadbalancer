package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.AttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ReactiveAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class GrayAttachReactiveWebFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    private ReactiveAttachHandler attachHandler;

    public GrayAttachReactiveWebFilter(GrayLoadBalancerProperties properties, ReactiveAttachHandler attachHandler) {

        this.properties = properties;
        this.attachHandler = attachHandler;

    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        List<GaryAttachParamater> paramaters = attachHandler.getAttachParamaters();
        AttachHandler.SimpleRequestData data = new AttachHandler.SimpleRequestData();
        if (CollectionUtils.isNotEmpty(paramaters)) {
            List<String> headers = new ArrayList<>();
            MediaType contentType = exchange.getRequest().getHeaders().getContentType();
            if (!HttpMethod.GET.equals(exchange.getRequest().getMethod())) {
                if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON) || contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
                    return ServerWebExchangeBodyUtil
                            .cache(exchange)
                            .doOnNext(object -> paramaters(object, data, paramaters))
                            .doOnNext((o) -> match(data, paramaters, headers))
                            .flatMap(object -> {
                                if (CollectionUtils.isNotEmpty(headers)) {
                                    ServerHttpRequest.Builder builder = object.getRequest().mutate();
                                    for (String header : headers) {
                                        builder.header(properties.getHeaderName(), header);
                                    }
                                    object = object.mutate().request(builder.build()).build();
                                }
                                return chain.filter(object);
                            });
                }
            }

        }
            return chain.filter(exchange);


    }

    /**
     * 收集参数
     *
     * @param exchange
     * @param paramaters
     * @return
     */
    protected void paramaters(ServerWebExchange exchange, AttachHandler.SimpleRequestData data, List<GaryAttachParamater> paramaters) {
        attachHandler.parser(paramaters, data, exchange);
    }

    /**
     * 匹配标签
     *
     * @param data
     * @param paramaters
     * @return
     */
    protected void match(AttachHandler.SimpleRequestData data, List<GaryAttachParamater> paramaters, List<String> result) {
        List<GaryAttachParamater> list = new ArrayList<>(paramaters);
        attachHandler.sort(list, data.getPath());
        for (GaryAttachParamater paramater : list) {
            if (attachHandler.match(paramater, data)) {
                String value = paramater.getValue();
                if (StringUtils.isNotBlank(value)) {
                    result.add(value);

                }
            }
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
