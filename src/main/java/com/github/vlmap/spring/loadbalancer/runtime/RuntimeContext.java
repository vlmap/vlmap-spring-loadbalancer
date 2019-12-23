/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.vlmap.spring.loadbalancer.runtime;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RuntimeContext is alive during the tracing context.
 * It will not be serialized to the collector, and always stays in the same context only.
 * <p>
 * In most cases, it means it only stays in a single thread for context propagation.
 *
 * @author wusheng, ascrutae
 */
public class RuntimeContext {
    private final ThreadLocal<RuntimeContext> contextThreadLocal;
    private Map context = new ConcurrentHashMap(0);
    public static final String REQUEST_TAG_REFERENCE = "REQUEST_TAG_REFERENCE";

    RuntimeContext(ThreadLocal<RuntimeContext> contextThreadLocal) {
        this.contextThreadLocal = contextThreadLocal;
    }

    public void put(Object key, Object value) {
        context.put(key, value);
    }

    public Object get(Object key) {
        return context.get(key);
    }

    public <T> T get(Object key, Class<T> type) {
        return (T) context.get(key);
    }

    public String getTag() {
        return get(REQUEST_TAG_REFERENCE, String.class);

    }

    public void setTag(String tag) {
        put(REQUEST_TAG_REFERENCE, tag);
    }

    public void remove(Object key) {
        context.remove(key);

        if (context.isEmpty()) {
            contextThreadLocal.remove();
        }
    }

    public void onComplete() {


        contextThreadLocal.remove();


    }

}
