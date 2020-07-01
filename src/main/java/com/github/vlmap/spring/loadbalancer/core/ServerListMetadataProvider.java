package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.core.GrayInfo;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.cloud.consul.discovery.ConsulServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ServerListMetadataProvider<T extends Server> {
    Map<T, GrayInfo> transform(List<T> servers);


    abstract class AbstractTransform<T extends Server> implements ServerListMetadataProvider<T> {
        protected IClientConfig config = null;
        private Map<Server, GrayInfo> caches = new ConcurrentHashMap<>();

        public AbstractTransform(IClientConfig config) {
            this.config = config;
        }

        public Map<T, GrayInfo> transform(List<T> servers) {
            if (CollectionUtils.isNotEmpty(servers)) {
                Map<T, GrayInfo> result = new HashMap<>(servers.size());
                for (T server : servers) {
                    GrayInfo object = transform(server);
                    if (object != null && CollectionUtils.isNotEmpty(object.getTags())) {
                        result.put(server, object);
                    }
                }
                return result;
            }
            return Collections.emptyMap();
        }

        public GrayInfo transform(T server) {
            Map<String, String> metadata = metadata(server);
            GrayInfo cache = caches.get(server);

            if (cache != null && ObjectUtils.equals(metadata, cache.getMetadata())) {
                return cache;

            }
            if (metadata != null) {
                GrayInfo object = parse(metadata);
                if (object != null) {
                    caches.put(server, object);
                } else {
                    if (cache != null) {
                        caches.remove(server);

                    }
                }
                return object;
            } else if (cache != null) {
                caches.remove(server);

            }
            return null;
        }


        protected GrayInfo parse(Map<String, String> metadata) {
            if (MapUtils.isNotEmpty(metadata)) {
                GrayInfo bean = new GrayInfo();

                EnvironmentUtils.binder(bean, metadata, "gray");

                if (CollectionUtils.isEmpty(bean.getTags())) {
                    return null;
                }
                bean.setMetadata(metadata);
                return bean;
            }
            return null;
        }

        protected abstract Map<String, String> metadata(T server);
    }

    class StaticServerListMetadataProvider extends AbstractTransform {
        public StaticServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        protected Map<String, String> metadata(Server server) {
            if (config != null) {
                Map<String, Object> properties = config.getProperties();
                Map<String, String> result = new HashMap<>();
                if (properties != null) {
                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        String key = entry.getKey();
                        if (StringUtils.startsWith(key, "matedata.")) {

                            result.put(StringUtils.substringBefore(key, "matedata."), ObjectUtils.toString(entry.getValue()));
                        }

                    }
                }


                return result;

            }
            return null;
        }
    }

    class EurekaServerListMetadataProvider extends AbstractTransform<DiscoveryEnabledServer> {
        public EurekaServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        public Map<String, String> metadata(DiscoveryEnabledServer server) {
            InstanceInfo instanceInfo = server.getInstanceInfo();
            if (instanceInfo != null) {
                return instanceInfo.getMetadata();
            }
            return null;
        }
    }

    class ConsulServerListMetadataProvider extends AbstractTransform<ConsulServer> {
        public ConsulServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        public Map<String, String> metadata(ConsulServer server) {

            return server.getMetadata();
        }
    }

    class NacosServerListMetadataProvider extends AbstractTransform<NacosServer> {
        public NacosServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        public Map<String, String> metadata(NacosServer server) {

            return server.getMetadata();
        }
    }
}
