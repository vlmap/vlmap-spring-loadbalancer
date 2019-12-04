package com.github.vlmap.spring.tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.core.env.*;

import java.util.LinkedHashSet;

public class DynamicToolProperties   {

    private Environment env;

    private SpringToolsProperties properties;

    private MapPropertySource propertySource;
    public DynamicToolProperties(Environment env,  SpringToolsProperties properties) {
        this.env = env;
        this.properties = properties;
    }


    public MapPropertySource getDefaultToolsProps() {
        if (propertySource == null) {
            propertySource = getPropertiySource(env, properties);
        }
        return propertySource;
    }



    private static MapPropertySource getPropertiySource(Environment environment, SpringToolsProperties properties) {

        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
            MutablePropertySources propertySources = env.getPropertySources();
            PropertySource propertySource = propertySources.get(PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME);
            if (CompositePropertySource.class.isInstance(propertySource)) {
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
