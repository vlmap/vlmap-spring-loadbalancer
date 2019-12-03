package com.github.vlmap.spring.tools.loadbalancer;


import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropertiesListener;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DelegatingLoadBalancer implements ILoadBalancer, InitializingBean {

    private AtomicReference<Map<String, Set<String>>> tagsInProgress = new AtomicReference(Collections.emptyMap());
    private IClientConfig clientConfig;
    private List<TagProcess> tagProcesses;
    private BaseLoadBalancer target;
    private DelegatePropChangeListener delegatePropChangeListener;

    public DelegatingLoadBalancer(IClientConfig clientConfig, BaseLoadBalancer target, List<TagProcess> tagProcesses) {
        this.target = target;
        this.tagProcesses = tagProcesses == null ? Collections.emptyList() : tagProcesses;
        this.clientConfig = clientConfig;
        if (CollectionUtils.isNotEmpty(tagProcesses)) {
            AnnotationAwareOrderComparator.sort(tagProcesses);

        }

    }

    public void setDelegatePropChangeListener(DelegatePropChangeListener delegatePropChangeListener) {
        this.delegatePropChangeListener = delegatePropChangeListener;
    }

    @Override
    public void addServers(List<Server> newServers) {
        target.addServers(newServers);
    }

    @Override
    public Server chooseServer(Object key) {
        return target.chooseServer(key);
    }

    @Override
    public void markServerDown(Server server) {
        target.markServerDown(server);
    }

    @Override
    public List<Server> getServerList(boolean availableOnly) {
        return (availableOnly ? getReachableServers() : getAllServers());
    }


    @Override
    public List<Server> getReachableServers() {
        List<Server> servers = target.getReachableServers();
        return processServers(servers);
    }

    @Override
    public List<Server> getAllServers() {

        List<Server> servers = target.getAllServers();
        return processServers(servers);

    }

    protected String tag() {
        for (TagProcess process : tagProcesses) {
            String tag = process.getRequestTag();
            if (StringUtils.isNotBlank(tag)) {
                return tag;
            }
        }
        return null;
    }

    protected List<Server> processServers(List<Server> servers) {

        Map<String, Set<String>> map = tagsInProgress.get();
        if (map.isEmpty()) {
            return servers;             // 如果所有节点都没配标签，返回所有列表，

        }
        String tagValue = tag();
        List<Server> list = new ArrayList<>(servers.size());

        if (StringUtils.isBlank(tagValue)) {
            //无标签请求，排除包含标签的节点

            for (Server server : servers) {
                Set<String> tags = map.get(server.getId());
                if (tags != null && tags.contains(tagValue)) {
                    list.add(server);
                }

            }
            return list;

        } else {
            //有标签的请求,优先匹配标签

            for (Server server : servers) {
                Set<String> tags = map.get(server.getId());
                if (tags != null && tags.contains(tagValue)) {
                    list.add(server);
                }

            }
            //匹配不到则返回，无标签节点
            if (list.isEmpty()) {
                for (Server server : servers) {
                    Set<String> tags = map.get(server.getId());
                    if (CollectionUtils.isEmpty(tags)) {
                        list.add(server);
                    }

                }
            }


        }

        return Collections.unmodifiableList(list);
    }

    public void tagStateInProgress() {


        Configuration configuration = ConfigurationManager.getConfigInstance().subset(clientConfig.getClientName());


        MapConfigurationPropertySource propertySource = new MapConfigurationPropertySource();
        Iterator<String> iterator = configuration.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = configuration.getString(key);
            propertySource.put(key, value);
        }

        Binder binder = new Binder(propertySource);
        RibbonTagOfServers ribbon = new RibbonTagOfServers();
        binder.bind("ribbon", Bindable.ofInstance(ribbon));

        List<RibbonTagOfServers.TagOfServers> tagOfServers = ribbon.getTagOfServers();
        if (tagOfServers != null) {
            Map<String, Set<String>> map = new HashMap<>(tagOfServers.size());

            for (RibbonTagOfServers.TagOfServers tagOfServer : tagOfServers) {

                if (tagOfServer != null && CollectionUtils.isNotEmpty(tagOfServer.getTags()) && org.apache.commons.lang.StringUtils.isNotBlank(tagOfServer.getId())) {
                    map.put(tagOfServer.getId(), tagOfServer.getTags());
                }
            }
            tagsInProgress.set(map);


        }


    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (delegatePropChangeListener != null) {
            PropertiesListener listener = new PropertiesListener(clientConfig.getClientName(), true, (PropertyChangeEvent event) -> {

                tagStateInProgress();


            });
            delegatePropChangeListener.addListener(listener);
        }
        tagStateInProgress();
    }
    public static class RibbonTagOfServers {

        List< TagOfServers> tagOfServers;

        public List< TagOfServers> getTagOfServers() {
            return tagOfServers;
        }

        public void setTagOfServers(List< TagOfServers> tagOfServers) {
            this.tagOfServers = tagOfServers;
        }

        public static class TagOfServers {
            private   String id;
            private   Set<String> tags;

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
