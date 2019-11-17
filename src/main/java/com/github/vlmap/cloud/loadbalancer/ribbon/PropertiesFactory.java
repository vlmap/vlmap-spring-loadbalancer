package com.github.vlmap.cloud.loadbalancer.ribbon;


import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;



public class PropertiesFactory extends org.springframework.cloud.netflix.ribbon.PropertiesFactory {
    static final String NAMESPACE = "ribbon";

    @Autowired
    private Environment environment;

    private Map<Class, String> _classToProperty = Collections.emptyMap();


    @PostConstruct
    public void init() {
        try {

            Map<Class, String> classToProperty = (Map)    FieldUtils.readField(this, "classToProperty", true);
            if (classToProperty != null) {
                this._classToProperty = classToProperty;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getClassName(Class clazz, String name) {
        String className = super.getClassName(clazz, name);
        if (StringUtils.isBlank(className) && this._classToProperty.containsKey(clazz)) {

            String classNameProperty = this._classToProperty.get(clazz);
            className = environment.getProperty(NAMESPACE + "." + classNameProperty);


        }

        return className;
    }
    public <C> C get(Class<C> clazz, IClientConfig config, String name) {
        C object=super.get(clazz,config,name);
        if(object!=null&& clazz.equals(IRule.class)){
            return (C)( new DelegatingTagRule((IRule) object));
        }
        return object;
    }
}
