package com.github.vlmap.spring.tools.common;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.core.env.*;

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
