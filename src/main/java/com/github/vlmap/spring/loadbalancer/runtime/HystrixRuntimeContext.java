package com.github.vlmap.spring.loadbalancer.runtime;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

import java.util.HashMap;
import java.util.Map;

public class HystrixRuntimeContext implements RuntimeContext {
    private final HystrixRequestVariableDefault<Map<String, Object>> context = new HystrixRequestVariableDefault<>();

    HystrixRuntimeContext() {
    }

    public Map<String, Object> getContext() {
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
    public void release() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().close();
        }
    }

}
