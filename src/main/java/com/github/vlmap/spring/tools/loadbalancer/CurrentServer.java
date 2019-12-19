package com.github.vlmap.spring.tools.loadbalancer;

import com.github.vlmap.spring.tools.context.GrayUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


public class CurrentServer {
   Logger logger= LoggerFactory.getLogger(this.getClass());
    private Set<String> clientServerTags;
    String id = null;
    String appName = null;
    private CountDownLatch latch=new CountDownLatch(1);
    public CurrentServer(Environment environment, InetUtils inetUtils) {
        String port=environment.getProperty("server.port","8080");
        String ip="127.0.0.1";
        try {
            ip=  GrayUtils.ip(inetUtils,"");
        }catch (Exception e){
            logger.info("GrayUtils.ip(inetUtils,\"\") error",e);
        }
        this.id=ip+":"+port;
        this.appName=environment.getProperty("spring.application.name","application");

    }
    @PostConstruct
    public void initMethod(){
        try{
            Map<String, Set<String>> tagOfServer = GrayUtils.tagOfServer(appName);
            if(tagOfServer!=null){
                clientServerTags = tagOfServer.get(id);

            }

        }finally {
            latch.countDown();
        }
    }

    @EventListener(InstanceRegisteredEvent.class)
    public void listener(InstanceRegisteredEvent event) {
        try{
            latch.await();

            Object config = event.getConfig();

            String clazzName=config.getClass().getName();
            if (clazzName.equals("org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties")) {
                NacosDiscoveryProperties properties = (NacosDiscoveryProperties) config;
                id = properties.getIp() + ":" + properties.getPort();
                appName = properties.getService();
            }else if(StringUtils.equals(clazzName,"org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig")){
                CloudEurekaInstanceConfig properties=(CloudEurekaInstanceConfig)config;
                id = properties.getIpAddress() + ":" + properties.getNonSecurePort();
                appName = properties.getAppname();
            }
            Map<String, Set<String>> tagOfServer = GrayUtils.tagOfServer(appName);
            if(tagOfServer!=null){
                clientServerTags = tagOfServer.get(id);

            }
        }catch (Exception e){

        }



    }



    public boolean isGrayServer() {
        return CollectionUtils.isNotEmpty(clientServerTags);
    }

    public boolean container(String tag) {
        if (clientServerTags != null) {
            return clientServerTags.contains(tag);
        }
        return false;
    }


}
