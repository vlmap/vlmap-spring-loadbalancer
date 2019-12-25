package com.github.vlmap.spring.loadbalancer.runtime;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

import java.util.HashMap;
import java.util.Map;

public class HystrixRuntimeContext implements RuntimeContext {
    private final HystrixRequestVariableDefault<Map<String, Object>> context = new HystrixRequestVariableDefault<>();

    private Map<String, Object> getContext() {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        Map<String, Object> context = this.context.get();
        if (context == null) {
            context = new HashMap<>();
            this.context.set(context);
        }
        return context;
    }

    @Override
    public void put(String key, Object value) {
        Map<String, Object> context = getContext();
        context.put(key, value);
    }

    @Override
    public Object get(String key) {
        Map<String, Object> context = getContext();
        return context.get(key);

    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Map<String, Object> context = getContext();
        return (T) context.get(key);
    }

    @Override
    public void remove(String key) {
        Map<String, Object> context = getContext();
        context.remove(key);
    }

    @Override
    public void onComplete() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().close();
        }
    }

}
