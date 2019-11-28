/*
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.vlmap.spring.tools.zookeeper;


import com.github.vlmap.spring.tools.zookeeper.listener.AbstractTreeCacheListener;
import com.github.vlmap.spring.tools.zookeeper.listener.PropTreeCacheListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that registers a {@link TreeCache} for each context. It publishes events upon
 * element change in Zookeeper.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public class PropWatcher implements Closeable {

    private static final Log log = LogFactory.getLog(PropWatcher.class);
    private AtomicBoolean running = new AtomicBoolean(false);

    private List<String> contexts;

    private CuratorFramework curator;



    private HashMap<String, TreeCache> caches;

    private ObjectProvider<? extends AbstractTreeCacheListener> provider;


    public PropWatcher(List<String> contexts, CuratorFramework curator, ObjectProvider<? extends AbstractTreeCacheListener> provider) {
        this.contexts = contexts;
        this.curator = curator;
        this.provider = provider;
    }



    @PostConstruct
    public void start() {

        if (this.running.compareAndSet(false, true)) {
            this.caches = new HashMap<>();
            if (CollectionUtils.isEmpty(this.contexts)) return;
            CompositePropertySource composite = new CompositePropertySource("composite");

            for (String context : this.contexts) {

                if (!context.startsWith("/")) {
                    context = "/" + context;
                }


                try {
                    TreeCache cache = TreeCache.newBuilder(this.curator, context).build();
                    AbstractTreeCacheListener listener = provider.getIfAvailable();
                    listener.setContext(context);
                    if (listener instanceof PropTreeCacheListener) {
                        MapPropertySource container=new MapPropertySource(context, new ConcurrentHashMap<>());
                        composite.addPropertySource(container);
                        PropTreeCacheListener propsListener=(PropTreeCacheListener)listener;
                        propsListener.setComposite(composite);
                        propsListener.setContainer(container);

                    }

                    cache.getListenable().addListener(listener);
                    cache.start();
                    this.caches.put(context, cache);
                    // no race condition since ZookeeperAutoConfiguration.curatorFramework
                    // calls curator.blockUntilConnected
                } catch (KeeperException.NoNodeException e) {
                    // no node, ignore
                } catch (Exception e) {
                    log.error("Error initializing listener for context " + context, e);
                }
            }
        }
    }

//    public void setPropertySource( PropertySource propertySource) {
//        this.composite = propertySource;
//    }

    @Override
    public void close() {
        if (this.running.compareAndSet(true, false)) {
            for (TreeCache cache : this.caches.values()) {
                cache.close();
            }
            this.caches = null;
        }
    }


}
