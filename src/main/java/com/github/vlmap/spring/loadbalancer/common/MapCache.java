package com.github.vlmap.spring.loadbalancer.common;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @param <K> 需要转换的对象
 * @param <V> 转换后的对象
 */
public class MapCache<K, V> {
    private Logger logger = LoggerFactory.getLogger(MapCache.class);

    public interface Mirror<K, V> {
        V invoker(K key);
        default boolean equals(K k1,K k2){
            return ObjectUtils.equals(k1,k2);
        }
    }

    private volatile SoftReference<ConcurrentHashMap<Object, Object[]>> reference = null;

    private Mirror<K, V> mirror;

    public MapCache(Mirror mirror) {
        this.mirror = mirror;
    }

    protected Map<Object, Object[]> cacheObject() {

        Map<Object, Object[]> cacheObject = null;
        if (reference == null) {
            synchronized (this) {
                if (reference == null) {
                    cacheObject = new ConcurrentHashMap<>();
                    reference = new SoftReference(cacheObject);
                } else {
                    cacheObject = reference.get();
                    if (cacheObject == null) {
                        cacheObject = new ConcurrentHashMap<>();
                        reference = new SoftReference(cacheObject);
                    }
                }

            }

        } else {

            cacheObject = reference.get();
            if (cacheObject == null) {
                synchronized (this) {
                    cacheObject = reference.get();
                    if (cacheObject == null) {
                        cacheObject = new ConcurrentHashMap<>();
                        reference = new SoftReference(cacheObject);
                    }

                }
            }


        }
        return cacheObject;
    }

    public V get(K key) {
        Map<Object, Object[]> caches = cacheObject();


        if (key == null) return null;
        Object[] ref = caches.get(key);
        V object = null;
        if (ref != null &&  mirror.equals(key, (K)ref[1])) {
            object = (V) ref[0];
            return object;

        }
        try {
            object = mirror.invoker(key);
        } catch (Exception e) {
            logger.error("mirror.invoker error", e);
        }

        caches.put(key, new Object[]{object, key});
        return object;
    }
}
