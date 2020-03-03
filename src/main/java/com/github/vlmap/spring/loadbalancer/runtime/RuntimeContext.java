package com.github.vlmap.spring.loadbalancer.runtime;

import java.util.Map;

public interface RuntimeContext {
    String REQUEST_TAG_REFERENCE = "REQUEST_TAG_REFERENCE";


    Map<String, Object> getContext();


    default void put(String key, Object value) {
        Map<String, Object> context = getContext();
        context.put(key, value);
    }


    default Object get(String key) {
        Map<String, Object> context = getContext();
        return context.get(key);

    }


    default <T> T get(String key, Class<T> type) {
        Map<String, Object> context = getContext();
        return (T) context.get(key);
    }


    default void remove(String key) {
        Map<String, Object> context = getContext();
        context.remove(key);
    }


    void release();

}
