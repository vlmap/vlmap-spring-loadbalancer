package com.github.vlmap.spring.loadbalancer.runtime;

public interface RuntimeContext {
    public static final String REQUEST_TAG_REFERENCE = "REQUEST_TAG_REFERENCE";

    void put(String key, Object value);

    Object get(String key);

    public <T> T get(String key, Class<T> type);

    public void remove(String key);

    public void onComplete();

}
