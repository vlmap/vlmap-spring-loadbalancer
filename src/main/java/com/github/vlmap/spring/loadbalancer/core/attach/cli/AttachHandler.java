package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GrayAttachCommandLineParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
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
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String ATTACH_COMMANDS_PREFIX = "vlmap.spring.loadbalancer.attach.commands";
    private GrayLoadBalancerProperties properties;

    public AttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        this.environment = environment;
        this.properties = properties;
    }

    public List<GaryAttachParamater> getAttachParamaters() {
        return attachParamaters;
    }

    @EventListener(EnvironmentChangeEvent.class)
    public void listener(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        boolean state = false;
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                if (StringUtils.startsWith(key, ATTACH_COMMANDS_PREFIX)) {
                    state = true;
                    break;
                }
            }
        }

        if (state) {
            List<String> commands = properties.getAttach().getCommands();

            List<GaryAttachParamater> list = new ArrayList<>();
            BindResult<List<String>> result = Binder.get(environment).bind(ATTACH_COMMANDS_PREFIX, Bindable.listOf(String.class));
            commands = result.orElse(null);

            if (CollectionUtils.isNotEmpty(commands)) {
                for (String expression : commands) {

                    GaryAttachParamater paramater = parser.parser(expression);
                    if (paramater != null && StringUtils.isNotBlank(paramater.getValue())) {
                        list.add(paramater);
                    }


                }
            }


            this.attachParamaters = list;
        }


    }


    public void sort(List<GaryAttachParamater> list, String path) {
        Comparator<String> comparator = pathMatcher.getPatternComparator(path);

        list.sort(new GaryAttachParamater.Comparator(comparator));

    }

    public boolean match(GaryAttachParamater paramater, SimpleRequestData data) {


        boolean state = false;

        state = matchPath(paramater.getPath(), data.getPath());
        if (!state) {
            return false;
        }
        state = container(data.getCookies(), paramater.getCookies());
        if (!state) {
            return false;
        }
        state = container(data.getHeaders(), paramater.getHeaders());
        if (!state) {
            return false;
        }
        state = container(data.getParams(), paramater.getParams());
        if (!state) {
            return false;
        }
        state = matchMethod(paramater.getMethod(), data.getMethod());
        if (!state) {
            return false;
        }

        state = matchJson(paramater.getJsonpath(), data.getJsonDocument());


        return state;
    }

    protected boolean matchJson(Map<String, String> jsonpaths, Object document) {
        if (MapUtils.isNotEmpty(jsonpaths)) {
            if (document != null) {
                for (Map.Entry<String, String> entry : jsonpaths.entrySet()) {
                    String path = entry.getKey();
                    try {
                        Object object = JsonPath.read(document, path);
                        if (!StringUtils.equals(ObjectUtils.toString(object), ObjectUtils.toString(entry.getValue()))) {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }

                }


            } else {
                return false;
            }
        }
        return true;

    }

    protected boolean matchPath(String pattern, String path) {
        if (StringUtils.isNotBlank(pattern)) {

            if (pathMatcher.match(pattern, path)) {
                return true;
            }

            return false;

        }
        return true;
    }

    protected boolean matchMethod(String method, String input) {
        if (StringUtils.isBlank(method)) return true;
        return StringUtils.equals(method, input);

    }


    protected boolean container(MultiValueMap<String, String> parent, Map<String, String> child) {
        if (MapUtils.isNotEmpty(child)) {
            if (MapUtils.isNotEmpty(parent)) {
                for (Map.Entry<String, String> entry : child.entrySet()) {
                    List<String> list = parent.get(entry.getKey());
                    if (!CollectionUtils.isEmpty(list) || !list.contains(entry.getValue())) {
                        return false;
                    }

                }
            } else {
                return false;
            }


        }
        return true;
    }

    protected boolean isReadBody(List<GaryAttachParamater> attachs) {


        if (this.properties.getAttach().isReadBody()) {
            for (GaryAttachParamater attach : attachs) {
                if (MapUtils.isNotEmpty(attach.getJsonpath())) {
                    return true;
                }


            }
        }
        return false;
    }

    public SimpleRequestData parser(List<GaryAttachParamater> attachs, SimpleRequestData data, HttpServletRequest request) {
        data.path = request.getRequestURI();
        data.method = request.getMethod();
        data.contentType = request.getContentType();
        MediaType contentType = MediaType.valueOf(data.contentType);


        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            data.cookies = new LinkedMultiValueMap<>();
            for (Cookie cookie : cookies) {
                String key = cookie.getName();
                String value = cookie.getValue();

                data.cookies.add(key, value);

            }
        }


        data.params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            data.params.put(entry.getKey(), Arrays.asList(entry.getValue()));

        }


        Enumeration<String> headerNames = request.getHeaderNames();
        data.headers = new LinkedMultiValueMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> list = EnumerationUtils.toList(request.getHeaders(headerName));
            data.headers.put(headerName, list);
        }

        if (isReadBody(attachs) && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            String charsetType = contentType.getSubtype();

            Charset charset = getCharset(charsetType);

            ServletInputStream input = null;
            try {

                input = request.getInputStream();
                data.body = IOUtils.toString(input, charset);

            } catch (Exception e) {

            } finally {
                IOUtils.closeQuietly(input);
            }


        }

        return null;
    }

    public Mono<SimpleRequestData> parser(@NotNull List<GaryAttachParamater> attachs, SimpleRequestData data, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        data.path = request.getPath().value();
        data.method = request.getMethod().name();
        data.contentType = request.getHeaders().getContentType().getType();

        MediaType contentType = request.getHeaders().getContentType();


        data.headers = new LinkedMultiValueMap<>();
        HttpHeaders headers = request.getHeaders();
        if (headers != null) {
            data.headers.addAll(headers);
        }

        data.cookies = new LinkedMultiValueMap<>();
        for (Map.Entry<String, List<HttpCookie>> entry : request.getCookies().entrySet()) {
            String key = entry.getKey();
            List<HttpCookie> values = entry.getValue();
            List<String> list = new ArrayList<>(values.size());
            values.stream().forEach(cookie -> list.add(cookie.getValue()));
            data.cookies.put(key, list);

            }


        Mono<SimpleRequestData> mono=Mono.just(data);

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

        if (isReadBody(attachs) && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {

            mono = mono.flatMap(object -> {
                return Mono.from(request.getBody().flatMap(dataBuffer -> {
                    String charsetType = contentType.getSubtype();

                    Charset charset = getCharset(charsetType);

                    CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
                    DataBufferUtils.release(dataBuffer);
                    String json = charBuffer.toString();
                    data.body = json;
                    return Mono.just(data);
                }));
            });

        }
         return mono;



    }


    private static Charset getCharset(String charset) {

        Charset object = null;
        if (StringUtils.isNotBlank(charset)) {
            try {
                object = Charset.forName(charset);
            } catch (Exception e) {
                logger.error("Charset.forName(charsetType) error,charsetType=" + charset, e);
            }
        }
        if (object == null) {
            object = DEFAULT_CHARSET;

        }
        return object;
    }

    public static class SimpleRequestData {


        private String method;
        private String path;
        private MultiValueMap<String, String> params;
        private MultiValueMap<String, String> headers;
        private MultiValueMap<String, String> cookies;
        private String body;
        private String contentType;

        private boolean parseJson = false;
        Object document;

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public Object getJsonDocument() {
            if (!parseJson) {
                parseJson = true;
                try {
                    if (StringUtils.isNotBlank(body) && StringUtils.equalsIgnoreCase(contentType, "application/json")) {
                        this.document = Configuration.defaultConfiguration().jsonProvider().parse(body);
                    }
                } catch (Exception e) {

                }
            }
            return false;
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
