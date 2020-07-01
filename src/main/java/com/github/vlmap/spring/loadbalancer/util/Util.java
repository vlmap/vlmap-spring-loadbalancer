package com.github.vlmap.spring.loadbalancer.util;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class Util {

    public static boolean isEnabled(GrayLoadBalancerProperties.Enabled enabled) {
        if (enabled != null) {
            return enabled.isEnabled();
        }
        return false;
    }


    public static <K, V> MultiValueMap addAll(MultiValueMap<K, V> container, MultiValueMap<K, V> values) {
        for (Map.Entry<K, List<V>> entry : values.entrySet()) {
            K key = entry.getKey();
            List<V> list = entry.getValue();
            if (list != null) {
                for (V value : list) {
                    container.add(key, value);
                }
            }


        }
        return container;

    }
}
