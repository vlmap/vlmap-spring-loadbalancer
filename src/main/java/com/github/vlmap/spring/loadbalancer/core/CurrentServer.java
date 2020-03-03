package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.util.GrayUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 从集群中获取当前服务的灰度配置
 */
public class CurrentServer {
    private Set<String> grayTags = Collections.emptySet();
    private String id = null;

    private String appName = null;
    private ConfigurableEnvironment environment;

    public CurrentServer(ConfigurableEnvironment environment) {
        this.environment = environment;

    }

    @EventListener(InstanceRegisteredEvent.class)
    public void listener(InstanceRegisteredEvent event) {

        Object config = event.getConfig();

        String clazzName = config.getClass().getName();

        if (clazzName.equals("org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties")) {
            NacosDiscoveryProperties properties = (NacosDiscoveryProperties) config;

            id = properties.getIp() + ":" + properties.getPort();

            appName = properties.getService();
        } else if (StringUtils.equals(clazzName, "org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean")) {

            CloudEurekaInstanceConfig properties = (CloudEurekaInstanceConfig) config;


            id = properties.getIpAddress() + ":" + properties.getNonSecurePort();
            appName = properties.getAppname();
        } else if (StringUtils.equals(clazzName, "org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties")) {

            ConsulDiscoveryProperties properties = (ConsulDiscoveryProperties) config;

            id = properties.getIpAddress() + ":" + properties.getPort();

            appName = properties.getServiceName();
        }

        bind();


    }

    protected void bind() {
        if (StringUtils.isBlank(appName) || StringUtils.isBlank(id)) return;
        Map<String, Set<String>> tagOfServer = GrayUtils.tagOfServer(environment, appName);
        Set<String> result = null;
        if (tagOfServer != null) {
            result = tagOfServer.get(this.id);
        }
        grayTags = result == null ? Collections.emptySet() : Collections.unmodifiableSet(result);
    }


    @EventListener(EnvironmentChangeEvent.class)

    protected void listener(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        if (CollectionUtils.isNotEmpty(keys)) {
            String prefix = StringUtils.upperCase(appName);
            for (String key : keys) {
                if (StringUtils.startsWith(key, prefix)) {
                    bind();
                    break;
                }
            }
        }

    }


    public boolean isGrayServer() {
        return CollectionUtils.isNotEmpty(grayTags);
    }


    public Collection<String> getGrayTags() {
        return grayTags;
    }

    public String getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }
}
