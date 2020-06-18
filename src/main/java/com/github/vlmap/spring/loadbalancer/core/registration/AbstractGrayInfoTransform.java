package com.github.vlmap.spring.loadbalancer.core.registration;

import com.github.vlmap.spring.loadbalancer.core.GrayInfo;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGrayInfoTransform<T extends Server> implements GrayInfoTransform<T> {
    private Map<Server, GrayInfo> caches = new ConcurrentHashMap<>();

    public Map<T, GrayInfo> transform(List<T> servers) {
        if (CollectionUtils.isNotEmpty(servers)) {
            Map<T, GrayInfo> result = new HashMap<>(servers.size());
            for (T server : servers) {
                GrayInfo object = transform(server);
                if (object != null && CollectionUtils.isNotEmpty(object.getTags())) {
                    result.put(server, object);
                }
            }
            return result;
        }
        return Collections.emptyMap();
    }

    public GrayInfo transform(T server) {
        Map<String, String> metadata = metadata(server);
        GrayInfo cache = caches.get(server);

        if (cache != null && ObjectUtils.equals(metadata, cache.getMetadata())) {
            return cache;

        }
        if (metadata != null) {
            GrayInfo object = parse(metadata);
            if (object != null) {
                caches.put(server, object);
            } else {
                if (cache != null) {
                    caches.remove(server);

                }
            }
            return object;
        } else if (cache != null) {
            caches.remove(server);

        }
        return null;
    }


    protected GrayInfo parse(Map<String, String> metadata) {
        if (MapUtils.isNotEmpty(metadata)) {
            MapConfigurationPropertySource propertySource = new MapConfigurationPropertySource(metadata);
            Binder binder = new Binder(propertySource);
            GrayInfo bean = new GrayInfo();
            binder.bind("gray", Bindable.ofInstance(bean));
            if(CollectionUtils.isEmpty(bean.getTags())){
                return null;
            }
            bean.setMetadata(metadata);
            return bean;
        }
        return null;
    }

    protected abstract Map<String, String> metadata(T server);
}
