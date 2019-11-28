package com.github.vlmap.spring.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.*;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DynamicToolProperties   {

    private Environment env;

    private SpringToolsProperties properties;

    private MapPropertySource defaultToolsProps;
    public DynamicToolProperties(Environment env,  SpringToolsProperties properties) {
        this.env = env;
        this.properties = properties;
    }


    @PostConstruct
    public void initMethod(){
        String  propertySourceName=properties.getPropertySourceName();
        if (StringUtils.isBlank(propertySourceName)) return;
        if (env instanceof ConfigurableEnvironment ) {
            ConfigurableEnvironment enviroment = (ConfigurableEnvironment) env;
            MutablePropertySources propertySources=  enviroment.getPropertySources();
            this.defaultToolsProps=(MapPropertySource)propertySources.get(propertySourceName);

        }

    }

    public MapPropertySource getDefaultToolsProps() {
        return defaultToolsProps;
    }

    public String getTagHeader() {
        if (defaultToolsProps != null) {
            return (String) defaultToolsProps.getProperty(properties.getTagLoadbalancer().getHeaderName());
        }
        return properties.getTagLoadbalancer().getHeader();
    }
    public String getTagHeaderName() {
        return properties.getTagHeaderName();
    }



    public SpringToolsProperties getProperties() {
        return properties;
    }


}
