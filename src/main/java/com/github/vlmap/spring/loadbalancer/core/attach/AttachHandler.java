package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GrayAttachCommandLineParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AttachHandler {
    private static Logger logger = LoggerFactory.getLogger(AttachHandler.class);

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected Environment environment;
    protected List<GaryAttachParamater> attachParamaters = Collections.emptyList();
    protected GrayAttachCommandLineParser parser = new GrayAttachCommandLineParser();
    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String ATTACH_COMMANDS_PREFIX = "vlmap.spring.loadbalancer.attach.commands";
    protected GrayLoadBalancerProperties properties;

    public AttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        this.environment = environment;
        this.properties = properties;
    }

    public List<GaryAttachParamater> getAttachParamaters() {
        return attachParamaters;
    }

    @Order
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

            Set<GaryAttachParamater> list = new LinkedHashSet<>();

            if (CollectionUtils.isNotEmpty(commands)) {
                for (int i = 0, length = commands.size(); i < length; i++) {
                    String expression = commands.get(i);

                    GaryAttachParamater paramater = parser.parser(expression);
                    if (logger.isInfoEnabled()) {
                        logger.info("command:" + expression + " , GaryAttachParamater:" + paramater.toString());
                    }


                    if (paramater != null && StringUtils.isNotBlank(paramater.getValue())) {
                        list.add(paramater);
                    }


                }
            }

            this.attachParamaters = Collections.unmodifiableList(new ArrayList<>(list));
            if (logger.isInfoEnabled()) {
                logger.info("List  GaryAttachParamater :" + attachParamaters);
            }
        }


    }

    /**
     * 匹配标签 返回到 result
     *
     * @param data
     * @param paramaters
     * @return
     */
    public void match(AttachHandler.SimpleRequestData data, List<GaryAttachParamater> paramaters, List<String> result) {
        List<GaryAttachParamater> list = new ArrayList<>(paramaters);
        this.sort(list, data.getPath());
        for (GaryAttachParamater paramater : list) {
            if (this.match(paramater, data)) {
                String value = paramater.getValue();
                if (StringUtils.isNotBlank(value)) {
                    result.add(value);

                }
            }
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
                    if (CollectionUtils.isNotEmpty(list) && list.contains(entry.getValue())) {
                        return true;
                    }

                }
                return false;
            } else {
                return false;
            }


        }
        return true;
    }

    public boolean isJsonRequest(MediaType contentType, HttpMethod method) {


        if (!(HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method))) {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {

                return true;


            }
        }

        return false;
    }


    public static class SimpleRequestData {


        String method;
        String path;
        MultiValueMap<String, String> params;
        MultiValueMap<String, String> headers;
        MultiValueMap<String, String> cookies;
        String body;
        String contentType;

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
                    logger.error("parse json error,json:" + body);
                }
            }
            return this.document;
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
