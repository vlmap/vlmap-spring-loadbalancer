package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Map;

public class DynamicToolProperties implements InitializingBean {

    private Environment env;

    private SpringToolsProperties properties;

    public DynamicToolProperties(Environment env,  SpringToolsProperties properties) {
        this.env = env;
        this.properties = properties;
    }

    private Map map = null;
    private PropertySource propertySource;
    public PropertySource getPropertySource() {
        return propertySource;
    }

    public String getTagHeader() {
        if (map != null) {
            return (String) map.get(properties.getTagLoadBalancer().getHeaderName());
        }
        return properties.getTagLoadBalancer().getHeader();
    }
    public String getTagHeaderName() {
        return properties.getTagHeaderName();
    }

    public void doAfterPropertiesSet(){
        String  propertySourceName=properties.getPropertySourceName();
        if (StringUtils.isBlank(propertySourceName)) return;
        if (env instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            PropertySource propertySource = configEnv.getPropertySources().get(propertySourceName);


            if (propertySource != null) {
                Object source = propertySource.getSource();
                if (source instanceof Map) {
                    map = (Map) source;
                }

            }
            if (map != null) {
                String header = properties.getTagLoadBalancer().getHeader();
                if (header != null) {
                    map.put(properties.getTagLoadBalancer().getHeaderName(), header);

                }
            }
            this.propertySource = propertySource;


        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        doAfterPropertiesSet();
    }
}
