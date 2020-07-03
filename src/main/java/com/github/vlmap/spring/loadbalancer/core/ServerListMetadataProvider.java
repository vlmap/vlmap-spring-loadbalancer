package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.common.MapCache;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.cloud.consul.discovery.ConsulServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ServerListMetadataProvider<T extends Server> {
    Map<T, GrayMetedata> transform(List<T> servers);


    abstract class AbstractServerListerMetadataProvider<T extends Server> implements ServerListMetadataProvider<T> {
        protected IClientConfig config = null;

        class MapCacheMirror implements MapCache.Mirror<T, GrayMetedata> {
            @Override
            public GrayMetedata invoker(T key) {
                Map<String, String> metadata = metadata(key);
                if (MapUtils.isEmpty(metadata)) return null;
                GrayMetedata bean = new GrayMetedata();
                EnvironmentUtils.binder(bean, metadata, "gray");
                return bean;
            }

            @Override
            public boolean equals(T k1, T k2) {
                return ObjectUtils.equals(k1==null?null:metadata(k1), k2==null?null:metadata(k2));
            }
        }

        private MapCache<T, GrayMetedata> cacheObject = new MapCache<>(new MapCacheMirror());


        public AbstractServerListerMetadataProvider(IClientConfig config) {
            this.config = config;
        }

        public Map<T, GrayMetedata> transform(List<T> servers) {
            if (CollectionUtils.isNotEmpty(servers)) {
                Map<T, GrayMetedata> result = new HashMap<>(servers.size());
                for (T server : servers) {
                    GrayMetedata object =cacheObject.get(server);
                    if (object != null && CollectionUtils.isNotEmpty(object.getTags())) {
                        result.put(server, object);
                    }
                }
                return result;
            }
            return Collections.emptyMap();
        }





        protected abstract Map<String, String> metadata(T server);
    }

    class StaticServerListMetadataProvider extends AbstractServerListerMetadataProvider {
        public StaticServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        protected Map<String, String> metadata(Server server) {
            if (config != null) {
                Map<String, Object> properties = config.getProperties();
                Map<String, String> result = new HashMap<>();
                if (properties != null) {
                    properties= EnvironmentUtils.getSubset(properties,"matedata",false);

                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        String key = entry.getKey();
                        result.put(key, ObjectUtils.toString(entry.getValue()));

                    }
                }


                return result;

            }
            return null;
        }
    }

    class EurekaServerListMetadataProvider extends AbstractServerListerMetadataProvider<DiscoveryEnabledServer> {
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

    class ConsulServerListMetadataProvider extends AbstractServerListerMetadataProvider<ConsulServer> {
        public ConsulServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        public Map<String, String> metadata(ConsulServer server) {

            return server.getMetadata();
        }
    }

    class NacosServerListMetadataProvider extends AbstractServerListerMetadataProvider<NacosServer> {
        public NacosServerListMetadataProvider(IClientConfig config) {
            super(config);
        }

        @Override
        public Map<String, String> metadata(NacosServer server) {

            return server.getMetadata();
        }
    }
}
