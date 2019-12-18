package com.github.vlmap.spring.tools.context;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesPropertySourceLocator implements PropertySourceLocator {

    private String name;
    private Object properties;

    public PropertiesPropertySourceLocator(String name, Object properties) {
        this.name = name;
        this.properties = properties;

    }


    @Override
    public PropertySource<?> locate(Environment environment) {

        Map<String, Object> map = new ConcurrentHashMap<>();

        String prefix = null;


        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(properties.getClass(),ConfigurationProperties.class);

        if (annotation != null) {
            prefix = annotation.prefix();
        }


        Binder.get(environment).bind(ConfigurationPropertyName.of(prefix), Bindable.ofInstance(properties), new BindHandler() {

            @Override
            public void onFinish(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) throws Exception {

                Class rawClass = target.getType().getRawClass();
                if (rawClass.isPrimitive() || CharSequence.class.isAssignableFrom(rawClass)) {
                    if (result == null) {
                        Object object = target.getValue().get();
                        if (object != null) {
                            map.put(name.toString(), object.toString());

                        }
                    } else {
                        map.put(name.toString(), result.toString());
                    }


                }

            }
        });
        MapPropertySource result = new MapPropertySource(name, map);


        return result;
    }
}
