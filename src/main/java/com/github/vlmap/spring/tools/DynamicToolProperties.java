package com.github.vlmap.spring.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DynamicToolProperties implements InitializingBean {

    private Environment env;

    private SpringToolsProperties properties;

    public DynamicToolProperties(Environment env,  SpringToolsProperties properties) {
        this.env = env;
        this.properties = properties;
    }


    private MapPropertySource defaultToolsProps;
    public MapPropertySource getDefaultToolsProps() {
        return defaultToolsProps;
    }

    public String getTagHeader() {
        if (defaultToolsProps != null) {
            return (String) defaultToolsProps.getProperty(properties.getTagLoadBalancer().getHeaderName());
        }
        return properties.getTagLoadBalancer().getHeader();
    }
    public String getTagHeaderName() {
        return properties.getTagHeaderName();
    }

    public void doAfterPropertiesSet(){
        String  propertySourceName=properties.getPropertySourceName();
        if (StringUtils.isBlank(propertySourceName)) return;
        if (env instanceof ConfigurableEnvironment ) {
            ConfigurableEnvironment enviroment = (ConfigurableEnvironment) env;
            MutablePropertySources propertySources=  enviroment.getPropertySources();
            PropertySource propertySource = propertySources.get(propertySourceName);
            if (MapPropertySource.class.isInstance(propertySource)&& ConcurrentMap.class.isInstance(propertySource.getSource())) {
                this.defaultToolsProps =(MapPropertySource)propertySource;
                return;
            }
            Map<String,Object> source=new ConcurrentHashMap<>();
            MapPropertySource result=new MapPropertySource(propertySourceName,source);
            this.defaultToolsProps =result;
            if(propertySource==null){
                propertySources.addLast(result);
            }else {
                if(EnumerablePropertySource.class.isInstance(propertySource)){
                    EnumerablePropertySource     enumerablePropertySource=(EnumerablePropertySource)propertySource;
                    for(String key:enumerablePropertySource.getPropertyNames()){
                        Object value=enumerablePropertySource.getProperty(key);
                        if(value!=null){
                            source.put(key,value);
                        }

                    }

                }
                propertySources.replace(propertySourceName,result);
            }

        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        doAfterPropertiesSet();
    }
}
