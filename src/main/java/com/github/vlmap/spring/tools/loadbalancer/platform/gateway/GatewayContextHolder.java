package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatewayContextHolder {
    private final static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    public static <T> T get(String key) {
        Map<String, Object> map = threadLocal.get();
        T value = null;
        if (map != null) {
            value = (T) map.get(key);
        }
        return value;
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();

        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Map<String, Object> attributes() {
        Map<String, Object> map = threadLocal.get();
        return map == null ? Collections.EMPTY_MAP : map;
    }

    public static void dispose() {
        threadLocal.remove();
    }

    public static ServerHttpRequest getRequest() {
        return get(REQUEST);
    }

    public static ServerHttpResponse getResponse() {
        return get(RESPONSE);
    }
}
