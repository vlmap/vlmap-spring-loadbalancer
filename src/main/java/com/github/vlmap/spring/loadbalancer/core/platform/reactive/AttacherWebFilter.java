package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.AttacherFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.RequestMatchParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.SimpleRequest;
import com.github.vlmap.spring.loadbalancer.util.RequestUtils;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AttacherWebFilter extends AttacherFilter implements WebFilter {


    public AttacherWebFilter(GrayLoadBalancerProperties properties) {

        super(properties);

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String headerName = properties.getHeaderName();
        String tag = exchange.getRequest().getHeaders().getFirst(headerName);
        if (!Util.isEnabled(this.properties.getAttacher()) || StringUtils.isNotBlank(tag)) {
            return chain.filter(exchange);
        }

        List<RequestMatchParamater> paramaters = getParamaters();
        SimpleRequest data = new SimpleRequest();
        if (CollectionUtils.isNotEmpty(paramaters)) {

            return initData(exchange, data)

                    .flatMap(object -> {
                        Object jsonDocument = RequestUtils.getJsonDocument(data);
                        RequestMatchParamater paramater = this.matcher.match(data, jsonDocument, paramaters);
                        if (paramater != null) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("apply attacher :" + paramater.toString());
                            }
                            String value = paramater.getValue();


                            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();

                            builder.header(headerName, value);


                            return chain.filter(exchange.mutate().request(builder.build()).build());


                        }
                        return chain.filter(exchange);
                    });
        }
        return chain.filter(exchange);


    }

    /**
     * 收集请求数据
     *
     *
     */
    protected Mono<SimpleRequest> initData(ServerWebExchange exchange, SimpleRequest data) {
        return parser(data, exchange);
    }

    public Mono<SimpleRequest> parser(SimpleRequest data, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        data.setPath(request.getPath().value());
        data.setMethod(request.getMethod().name());


        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        data.setHeaders(map);
        HttpHeaders headers = request.getHeaders();
        if (headers != null) {
            Util.addAll(map, headers);

        }
        map = new LinkedMultiValueMap<>();
        data.setCookies(map);
        for (Map.Entry<String, List<HttpCookie>> entry : request.getCookies().entrySet()) {
            String key = entry.getKey();
            List<HttpCookie> values = entry.getValue();
            List<String> list = new ArrayList<>(values.size());
            values.stream().forEach(cookie -> list.add(cookie.getValue()));
            map.put(key, list);

        }


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        data.setParams(params);
        Util.addAll(params, request.getQueryParams());

        MediaType contentType = request.getHeaders().getContentType();

        if (ObjectUtils.equals(exchange.getAttribute(ReadBodyFilter.READ_BODY_TAG), Boolean.TRUE)) {
            return Mono.just(data).flatMap((o) -> {

                return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {

                    Charset charset = null;
                    if (contentType != null) {
                        charset = contentType.getCharset();
                    }
                    charset = charset == null ? StandardCharsets.UTF_8 : charset;

                    CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
                    DataBufferUtils.release(dataBuffer);
                    String body = charBuffer.toString();
                    data.setBody(body);
                    return Mono.just(data);
                });
            }).flatMap(o -> {


                if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                    return exchange.getFormData().map(formData -> {
                        Util.addAll(params, formData);

                        return data;
                    });
                }
                return Mono.just(data);


            });
        } else {
            return Mono.just(data);
        }


    }


}
