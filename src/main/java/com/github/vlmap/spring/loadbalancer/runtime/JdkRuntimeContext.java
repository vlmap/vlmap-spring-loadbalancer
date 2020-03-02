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


import java.util.HashMap;
import java.util.Map;

/**
 * RuntimeContext is alive during the tracing context.
 * It will not be serialized to the collector, and always stays in the same context only.
 * <p>
 * In most cases, it means it only stays in a single thread for context propagation.
 *
 * @author wusheng, ascrutae
 */
public class JdkRuntimeContext implements RuntimeContext {
    private final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    JdkRuntimeContext() {
    }

    public Map<String, Object> getContext() {
        Map<String, Object> context = this.context.get();
        if (context == null) {
            context = new HashMap<>();
            this.context.set(context);
        }
        return context;
    }


    public void release() {


        this.context.remove();


    }

}
