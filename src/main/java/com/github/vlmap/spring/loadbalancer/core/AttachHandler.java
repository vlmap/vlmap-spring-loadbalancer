package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.core.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.cli.GrayAttachCommandLineParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AttachHandler {
    private static Logger logger = LoggerFactory.getLogger(AttachHandler.class);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Environment environment;
    List<GaryAttachParamater> attachParamaters = Collections.emptyList();
    GrayAttachCommandLineParser parser = new GrayAttachCommandLineParser();

    public AttachHandler(Environment environment) {
        this.environment = environment;
    }

    public List<GaryAttachParamater> getAttachParamaters() {
        return attachParamaters;
    }

    @EventListener(EnvironmentChangeEvent.class)
    public void listener(EnvironmentChangeEvent event) {
        List<GaryAttachParamater> list = new ArrayList<>();
        Set<String> keys = event.getKeys();


        BindResult<List<String>> result = Binder.get(environment).bind("vlmap.spring.loadbalancer.attach.el", Bindable.listOf(String.class));
        List<String> expressions = result.orElse(null);

        if (CollectionUtils.isNotEmpty(expressions)) {
            for (String expression : expressions) {

                GaryAttachParamater paramater = parser.parser(expression);
                if (paramater != null) {
                    list.add(paramater);
                }


            }
        }


        this.attachParamaters = list;

    }

    public  String attach(GaryAttachParamater paramater, SimpleRequestData data) {
        boolean strict = paramater.isStrict();
        return null;

    }

    protected String allAttach(GaryAttachParamater paramater, SimpleRequestData data) {
        Map<String, String> map = paramater.getCookies();

        if (MapUtils.isNotEmpty(map)) {
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                List<HttpCookie> list = cookies.get(entry.getKey());
                String value = list.stream().filter(cookie -> StringUtils.equals(cookie.getValue(), entry.getValue())).map(cookie -> cookie.getValue()).findFirst().orElse(null);
                if (value == null) {
                    return null;
                }
            }


        }
        map = paramater.getHeaders();

        if (MapUtils.isNotEmpty(map)) {
            HttpHeaders headers = exchange.getRequest().getHeaders();

            for (Map.Entry<String, String> entry : map.entrySet()) {

                List<String> list = headers.get(entry.getKey());
                if (CollectionUtils.isNotEmpty(list)) {
                    if (!list.contains(entry.getValue())) {
                        return null;
                    }
                } else {
                    return null;
                }

            }


        }
        List<String> list = paramater.getMethods();
        if (CollectionUtils.isNotEmpty(list)) {
            if (!list.contains(exchange.getRequest().getMethod().name())) {
                return null;
            }
        }

        Map<String, String> params = paramater.getParams();
        if (MapUtils.isNotEmpty(params)) {
            MultiValueMap<String, String> _params = new LinkedMultiValueMap();
            _params.addAll(exchange.getRequest().getQueryParams());

        }


        return null;
    }

    public static SimpleRequestData parser(List<GaryAttachParamater> attachs, SimpleRequestData data, HttpServletRequest request) {

        return null;
    }

    public static Mono<SimpleRequestData> parser(@NotNull List<GaryAttachParamater> attachs, SimpleRequestData data, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        data.uri = request.getPath().value();
        data.method = request.getMethod().name();
        data.contentType = request.getHeaders().getContentType().getType();

        MediaType contentType = request.getHeaders().getContentType();
        List<Flux<SimpleRequestData>> fluxes = new ArrayList<>(2);
        boolean useJson = false;
        boolean useCookie = false;
        boolean useParam = false;
        for (GaryAttachParamater attach : attachs) {
            if (!useJson && MapUtils.isNotEmpty(attach.getJsonpath())) {
                useJson = true;
            }
            if (!useCookie && MapUtils.isNotEmpty(attach.getCookies())) {
                useCookie = true;
            }
            if (!useParam && MapUtils.isNotEmpty(attach.getParams())) {
                useParam = true;
            }
            if (useJson && useCookie && useParam) {
                break;
            }
        }
        Mono<SimpleRequestData> mono=Mono.just(data);
        if (useParam) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.addAll(request.getQueryParams());

            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                mono = mono.flatMap(object -> {
                  return   exchange.getFormData().map(formData -> {
                        params.addAll(formData);
                        return data;
                    });

                });


            }
        }
        if (useJson && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {

             mono.flatMap(object-> {
                   request.getBody().map(dataBuffer -> {
                     String charsetType = contentType.getSubtype();

                     Charset charset = null;
                     if (StringUtils.isNotBlank(charsetType)) {
                         try {
                             charset = Charset.forName(charsetType);
                         } catch (Exception e) {
                             logger.error("Charset.forName(charsetType) error,charsetType=" + charsetType, e);
                         }
                     }
                     if (charset == null) {
                         charset = DEFAULT_CHARSET;

                     }
                     CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
                     DataBufferUtils.release(dataBuffer);
                     String json = charBuffer.toString();
                     data.body = json;
                     return Mono.just(data);
                 });
             };
            mono=mono.map(object->{

                 return Mono.then(flux)
            });
        }
         return mono;



    }

    public static class SimpleRequestData {


        private String method;
        private String uri;
        private MultiValueMap<String, String> params;
        private MultiValueMap<String, String> headers;
        private MultiValueMap<String, String> cookies;
        private String body;
        private String contentType;

        public String getMethod() {
            return method;
        }

        public String getUri() {
            return uri;
        }

        public MultiValueMap<String, String> getParams() {
            return params;
        }

        public MultiValueMap<String, String> getHeaders() {
            return headers;
        }

        public MultiValueMap<String, String> getCookies() {
            return cookies;
        }

        public String getBody() {
            return body;
        }


    }
}
