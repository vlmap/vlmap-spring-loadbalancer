package com.github.vlmap.spring.loadbalancer.core.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.registry.NacosRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.consul.discovery.ConsulServerUtils;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;

import javax.annotation.PostConstruct;
import java.util.Map;

public class MetaDataProvider {
    private Registration registration;
    IMetaDataProvider metaDataProvider = null;

    public MetaDataProvider(Registration registration) {
        this.registration = registration;
    }

    @PostConstruct
    protected void initMethod() {
        if (registration != null) {
            String clazzName = registration.getClass().getName();
            if (clazzName.equals("org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration")) {
                metaDataProvider = new EurekaMetaDataProvider();
            } else if (clazzName.equals("org.springframework.cloud.alibaba.nacos.registry.NacosRegistration")) {
                metaDataProvider = new NacosMetaDataProvider();
            } else if (clazzName.equals("org.springframework.cloud.consul.serviceregistry.ConsulRegistration")) {
                metaDataProvider = new ConsulMetaDataProvider();

            }
        }

    }


    public Map<String, String> metadata() {

        if (registration != null && metaDataProvider != null) {
            return metaDataProvider.metadata(registration);
        }
        return null;
    }

    static class ConsulMetaDataProvider implements IMetaDataProvider {

        @Override
        public Map<String, String> metadata(Registration object) {

            ConsulRegistration registration = (ConsulRegistration) object;
            return ConsulServerUtils.getMetadata(registration.getService().getTags());

        }
    }

    static class EurekaMetaDataProvider implements IMetaDataProvider {

        @Override
        public Map<String, String> metadata(Registration object) {

            EurekaRegistration registration = (EurekaRegistration) object;
            return registration.getInstanceConfig().getMetadataMap();


        }
    }

    static class NacosMetaDataProvider implements IMetaDataProvider {

        @Override
        public Map<String, String> metadata(Registration object) {

            NacosRegistration registration = (NacosRegistration) object;
            return registration.getNacosDiscoveryProperties().getMetadata();


        }
    }

    interface IMetaDataProvider {
        Map<String, String> metadata(Registration instanceConfig);
    }
}
