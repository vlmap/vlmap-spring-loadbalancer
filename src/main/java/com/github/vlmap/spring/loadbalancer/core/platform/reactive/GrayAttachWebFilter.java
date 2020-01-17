package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.AttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ReactiveAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class GrayAttachWebFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    private ReactiveAttachHandler attachHandler;

    @Autowired
    HttpHandler httpHandler;
    ServerCodecConfigurer serverCodecConfigurer = ServerCodecConfigurer.create();

    public GrayAttachWebFilter(GrayLoadBalancerProperties properties, ReactiveAttachHandler attachHandler) {

        this.properties = properties;
        this.attachHandler = attachHandler;

    }

    @PostConstruct
    public void initMethod() {
        if (httpHandler instanceof HttpWebHandlerAdapter) {
            HttpWebHandlerAdapter httpWebHandler = (HttpWebHandlerAdapter) httpHandler;
            serverCodecConfigurer = httpWebHandler.getCodecConfigurer();
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!this.properties.getAttach().isEnabled()) {
            return chain.filter(exchange);
        }
        List<GaryAttachParamater> paramaters = attachHandler.getAttachParamaters();
        AttachHandler.SimpleRequestData data = new AttachHandler.SimpleRequestData();
        if (CollectionUtils.isNotEmpty(paramaters)) {
            List<String> headers = new ArrayList<>();
            MediaType contentType = exchange.getRequest().getHeaders().getContentType();
            HttpMethod method = exchange.getRequest().getMethod();
            Mono<ServerWebExchange> mono = null;


            if (attachHandler.useCache(paramaters, contentType, method)) {
                //缓存body
                mono = ServerWebExchangeBodyUtil.cache(exchange, serverCodecConfigurer);
            }
            if (mono == null) {

                mono = Mono.just(exchange);

            }
            return mono
                    .flatMap(object -> paramaters(object, data, paramaters).thenReturn(object))

                    .doOnNext((o) -> attachHandler.match(data, paramaters, headers))
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
        return chain.filter(exchange);


    }

    /**
     * 收集参数
     *
     * @param exchange
     * @param paramaters
     * @return
     */
    protected Mono<AttachHandler.SimpleRequestData> paramaters(ServerWebExchange exchange, AttachHandler.SimpleRequestData data, List<GaryAttachParamater> paramaters) {
        return attachHandler.parser(paramaters, data, exchange);
    }


    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}
