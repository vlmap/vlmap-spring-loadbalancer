package com.github.vlmap.cloud.loadbalancer.config;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ConfigurationProperties(prefix = "spring.tools")

public class SpringToolsProperties  implements InitializingBean {
    private String propertySource="stateprops";
    @Autowired
    private Environment env;
    public String getPropertySource() {
        return propertySource;
    }

    public void setPropertySource(String propertySource) {
        this.propertySource = propertySource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        Environment env = getEnvironment();

        String propertySourceName= this.propertySource;
        if (ConfigurableEnvironment.class.isInstance(env)) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            PropertySource propertySource = configEnv.getPropertySources().get(propertySourceName);
            ConcurrentMap<String, Object> stateprops = null;

            if (propertySource == null) {
                stateprops = new ConcurrentHashMap<>();

                propertySource = new MapPropertySource(propertySourceName, stateprops);
                configEnv.getPropertySources().addLast(propertySource);

            }else if(MapPropertySource.class.isInstance(propertySource)){
                MapPropertySource  object=(MapPropertySource)propertySource;
                if(ConcurrentMap.class.isInstance(object.getSource())){
                    stateprops=(ConcurrentMap)object.getSource();
                }
            }else if(!MapPropertySource.class.isInstance(propertySource)&& EnumerablePropertySource.class.isInstance(propertySource) ){
                stateprops = new ConcurrentHashMap<>();

                EnumerablePropertySource enumerablePropertySource=(EnumerablePropertySource)propertySource;
                String[] names=enumerablePropertySource.getPropertyNames();
                if(names!=null){
                    for(String name:names){
                        stateprops.put(name,propertySource.getProperty(name));
                    }
                }
                configEnv.getPropertySources().replace(propertySourceName,propertySource);


            }
            String tag = env.getProperty(TagProcess.LOADBALANCER_TAG);
            if (StringUtils.isBlank(tag)) {
                stateprops.put("loadbalancer.tag", "Loadbalancer-Tag");
            }


        }
    }
}
