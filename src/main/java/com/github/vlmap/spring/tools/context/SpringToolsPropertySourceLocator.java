package com.github.vlmap.spring.tools.context;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringToolsPropertySourceLocator implements PropertySourceLocator {


    @Override
    public PropertySource<?> locate(Environment environment) {
        String propertySourceName = environment.getProperty("spring.tools.property-source-name");
        if (StringUtils.isBlank(propertySourceName)) {
            propertySourceName = SpringToolsProperties.DEFAULT_TOOLS_PROPERTIES_NAME;
        }


        Map<String, Object> map = new ConcurrentHashMap<>();
        SpringToolsProperties properties = new SpringToolsProperties();

        Binder.get(environment).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties), new BindHandler() {

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
        MapPropertySource result = new MapPropertySource(propertySourceName, map);


        return result;
    }
}
