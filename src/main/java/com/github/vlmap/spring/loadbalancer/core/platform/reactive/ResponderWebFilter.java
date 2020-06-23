package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderParamater;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ResponderWebFilter extends ResponderFilter implements WebFilter {


    public ResponderWebFilter(GrayLoadBalancerProperties properties) {

        super(properties);

    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String tag = exchange.getRequest().getHeaders().getFirst(this.properties.getHeaderName());
        if (Util.isEnabled(this.properties.getResponder()) && StringUtils.isNotBlank(tag)) {

            ResponderParamater data = getParamater(this.paramaters, tag);
            if (data != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Apply Responder:" + data.toString());
                }
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.valueOf(data.getCode()));
                MultiValueMap<String, String> cookies = data.getCookies();
                if (cookies != null) {

                    for (Map.Entry<String, List<String>> entry : cookies.entrySet()) {
                        String name = entry.getKey();
                        List<String> values = entry.getValue();
                        for (String value : values) {
                            response.addCookie(ResponseCookie.from(name, value).build());
                        }
                    }


                }
                MultiValueMap<String, String> headers = data.getHeaders();
                if (headers != null) {
                    response.getHeaders().addAll(headers);

                }
                String body = data.getBody();
                if (body != null) {
                    Charset charset = getAcceptCharset(response);
                    byte[] bytes = body.getBytes(charset);
                    response.getHeaders().setContentLength(bytes.length);
                    DataBuffer wrap = response.bufferFactory().wrap(bytes);
                    MediaType contentType = response.getHeaders().getContentType();
                    if (contentType == null) {

                        response.getHeaders().setContentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=" + charset.name()));

                    }
                    return response.writeWith(Flux.just(wrap));
                }
                return response.setComplete();
            }


        }


        return chain.filter(exchange);
    }

    protected Charset getAcceptCharset(ServerHttpResponse response) {
        List<Charset> list = response.getHeaders().getAcceptCharset();
        if (list.isEmpty()) {
            return StandardCharsets.UTF_8;
        }
        return list.get(0);
    }
}
