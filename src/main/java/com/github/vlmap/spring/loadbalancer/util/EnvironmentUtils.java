package com.github.vlmap.spring.loadbalancer.util;


import com.github.vlmap.spring.loadbalancer.core.platform.Platform;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class EnvironmentUtils {

    private static BeanBinder binder = Platform.isSpringBoot_2() ? new PropertiesBeanBinder() : new OldPropertiesBeanBinder();

    public static List<String> getKeys(ConfigurableEnvironment environment) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, PropertySource<?>> entry : getPropertySources(environment).entrySet()) {
            PropertySource<?> source = entry.getValue();
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
                for (String name : enumerable.getPropertyNames()) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    public static Map<String, String> getSubset(ConfigurableEnvironment environment, String prefix, boolean withPrefix) {
        Map<String, String> map = new HashMap<>();
        List<String> keys = getKeys(environment);
        String delimiter = ".";
        for (String key : keys) {
            String childKey = toSubsetKey(key, prefix, delimiter);
            if (childKey != null) {
                if (withPrefix) {

                    map.put(key, environment.getProperty(key));

                } else {
                    map.put(childKey, environment.getProperty(key));

                }
            }
        }
        return map;

    }


    public static String toSubsetKey(String key, String prefix, String delimiter) {
        if (!key.startsWith(prefix)) {
            return null;
        } else {
            String modifiedKey = null;
            if (key.length() == prefix.length()) {
                modifiedKey = "";
            } else {
                int i = prefix.length() + (delimiter != null ? delimiter.length() : 0);
                modifiedKey = key.substring(i);
            }

            return modifiedKey;
        }

    }


    private static Map<String, PropertySource<?>> getPropertySources(ConfigurableEnvironment environment) {
        Map<String, PropertySource<?>> map = new LinkedHashMap<>();
        MutablePropertySources sources = (environment != null
                ? environment.getPropertySources()
                : new StandardEnvironment().getPropertySources());
        for (PropertySource<?> source : sources) {
            extract("", map, source);
        }
        return map;
    }

    private static void extract(String root, Map<String, PropertySource<?>> map,
                                PropertySource<?> source) {
        if (source instanceof CompositePropertySource) {
            for (PropertySource<?> nest : ((CompositePropertySource) source)
                    .getPropertySources()) {
                extract(source.getName() + ":", map, nest);
            }
        } else {
            map.put(root + source.getName(), source);
        }
    }

    public static <T> T binder(T target, Map source, String prefix) {
        return (T) binder.bind(target, source, prefix);
    }


    interface BeanBinder<T> {
        T bind(T target, Map<String, Object> map, String prefix);
    }

    static class OldPropertiesBeanBinder<T> implements BeanBinder<T> {

        @Override
        public T bind(T target, Map<String, Object> map, String prefix) {
            PropertiesConfigurationFactory factory = new PropertiesConfigurationFactory(target);
            MapPropertySource propertySource = new MapPropertySource("binder", map);
            MutablePropertySources propertySources = new MutablePropertySources();
            propertySources.addLast(propertySource);
            factory.setPropertySources(propertySources);
            if (StringUtils.hasLength(prefix)) {
                factory.setTargetName(prefix);
            }
            try {
                factory.bindPropertiesToTarget();
            } catch (Exception ex) {
                String targetClass = ClassUtils.getShortName(target.getClass());
                throw new BeanCreationException(targetClass, "Could not bind properties to " + targetClass, ex);
            }
            return target;
        }
    }

    static class PropertiesBeanBinder<T> implements BeanBinder<T> {
        @Override
        public T bind(T target, Map<String, Object> map, String prefix) {
            MapConfigurationPropertySource propertySource = new MapConfigurationPropertySource(map);
            Binder binder = new Binder(propertySource);
            binder.bind(prefix, Bindable.ofInstance(target));

            return target;
        }


    }

}
