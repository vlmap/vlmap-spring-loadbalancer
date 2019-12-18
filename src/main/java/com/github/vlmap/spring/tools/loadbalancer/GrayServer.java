package com.github.vlmap.spring.tools.loadbalancer;

import com.netflix.config.ConfigurationManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import javax.annotation.PostConstruct;
import java.util.*;

public class GrayClientServer {
    private String clientName;
    Map<String, Set<String>> clientServerTags;

    public Map<String, Set<String>> getClientServerTags() {
        return clientServerTags;
    }

    public GrayClientServer(String clientName) {
        this.clientName = clientName;
    }

    @PostConstruct
    public void initMethod() {


        Configuration configuration = ConfigurationManager.getConfigInstance().subset(clientName);


        MapConfigurationPropertySource propertySource = new MapConfigurationPropertySource();
        Iterator<String> iterator = configuration.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = configuration.getString(key);
            propertySource.put(key, value);
        }

        Binder binder = new Binder(propertySource);
        RibbonGrayOfServersProperties ribbon = new RibbonGrayOfServersProperties();
        binder.bind("ribbon", Bindable.ofInstance(ribbon));

        List<RibbonGrayOfServersProperties.TagOfServers> tagOfServers = ribbon.getTagOfServers();
        if (tagOfServers != null) {
            Map<String, Set<String>> map = new HashMap<>(tagOfServers.size());

            for (RibbonGrayOfServersProperties.TagOfServers tagOfServer : tagOfServers) {

                if (tagOfServer != null && CollectionUtils.isNotEmpty(tagOfServer.getTags()) && StringUtils.isNotBlank(tagOfServer.getId())) {
                    map.put(tagOfServer.getId(), tagOfServer.getTags());
                }
            }
            this.clientServerTags = Collections.unmodifiableMap(map);


        } else {
            this.clientServerTags = null;
        }


    }

    /**
     * 灰度路由，服务配置
     */
//@ConfigurationProperties(prefix = "MICRO-CLOUD-SERVER.ribbon")
    public static class RibbonGrayOfServersProperties {


        List<TagOfServers> tagOfServers;

        public List<TagOfServers> getTagOfServers() {
            return tagOfServers;
        }

        public void setTagOfServers(List<TagOfServers> tagOfServers) {
            this.tagOfServers = tagOfServers;
        }


        public static class TagOfServers {
            private String id;
            private Set<String> tags;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Set<String> getTags() {
                return tags;
            }

            public void setTags(Set<String> tags) {
                this.tags = tags;
            }
        }
    }

}
