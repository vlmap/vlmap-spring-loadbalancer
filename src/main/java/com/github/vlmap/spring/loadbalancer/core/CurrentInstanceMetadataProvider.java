package com.github.vlmap.spring.loadbalancer.core;

import com.netflix.appinfo.EurekaInstanceConfig;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.ConsulServerUtils;

import java.util.Map;

/**
 * 获取当前示例的  metadata
 */
public interface CurrentInstanceMetadataProvider {

    Map<String, String> metadata();



    class EurekaCurrentInstanceMetadataProvider implements CurrentInstanceMetadataProvider {
        private EurekaInstanceConfig instanceConfig;

        public EurekaCurrentInstanceMetadataProvider(EurekaInstanceConfig instanceConfig) {
            this.instanceConfig = instanceConfig;
        }

        @Override
        public Map<String, String> metadata() {

            return instanceConfig.getMetadataMap();


        }
    }


    class ConsulCurrentInstanceMetadataProvider implements CurrentInstanceMetadataProvider {
        ConsulDiscoveryProperties properties;

        public ConsulCurrentInstanceMetadataProvider(ConsulDiscoveryProperties properties) {
            this.properties = properties;
        }

        @Override
        public Map<String, String> metadata() {

            return ConsulServerUtils.getMetadata(properties.getTags());
        }
    }

    class NacosCurrentInstanceMetadataProvider implements CurrentInstanceMetadataProvider {

        NacosDiscoveryProperties properties;

        public NacosCurrentInstanceMetadataProvider(NacosDiscoveryProperties properties) {
            this.properties = properties;
        }

        @Override
        public Map<String, String> metadata() {


            return properties.getMetadata();


        }
    }
}
