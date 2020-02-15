package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactiveAttachHandler extends AbstractAttachHandler {

    public ReactiveAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        super(properties, environment);
    }

    public Mono<SimpleRequestData> parser(SimpleRequestData data, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        data.setPath(request.getPath().value());
        data.setMethod(request.getMethod().name());
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType != null) {
            data.setContentType(contentType.getType() + "/" + contentType.getSubtype());
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        data.setHeaders(map);
        HttpHeaders headers = request.getHeaders();
        if (headers != null) {
            map.addAll(headers);
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
        params.addAll(request.getQueryParams());

        if (ObjectUtils.equals(exchange.getAttribute(ReadBodyFilter.READ_BODY_TAG), Boolean.TRUE)) {
            return Mono.just(data).flatMap((o) -> {
                if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                    return exchange.getFormData().map(formData -> {
                        params.addAll(formData);
                        return data;
                    });
                }
                return Mono.just(data);
            }).flatMap(o -> {
                if (isJsonRequest(contentType, request.getMethod())) {
                    return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {

                        Charset charset = contentType.getCharset();
                        charset = charset == null ? AbstractAttachHandler.DEFAULT_CHARSET : charset;

                        CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
                        DataBufferUtils.release(dataBuffer);
                        String json = charBuffer.toString();
                        data.setBody(json);
                        return Mono.just(data);
                    });
                }
                return Mono.just(data);

            });
        } else {
            return Mono.just(data);
        }


    }

}
