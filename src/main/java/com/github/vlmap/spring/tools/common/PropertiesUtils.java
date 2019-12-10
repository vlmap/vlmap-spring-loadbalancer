package com.github.vlmap.spring.tools.common;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.netflix.config.ConfigurationManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.core.env.*;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PropertiesUtils {


    public static MapPropertySource getPropertiesSource(Environment environment, SpringToolsProperties properties) {

        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
            MutablePropertySources propertySources = env.getPropertySources();
            PropertySource propertySource = propertySources.get(PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME);
            if (propertySource instanceof CompositePropertySource) {
                CompositePropertySource composite = (CompositePropertySource) propertySource;
                for (PropertySource ps : composite.getPropertySources()) {
                    if (StringUtils.equals(properties.getPropertySourceName(), ps.getName())) {
                        return (MapPropertySource) ps;
                    }
                }
            }


        }
        return null;
    }
}
