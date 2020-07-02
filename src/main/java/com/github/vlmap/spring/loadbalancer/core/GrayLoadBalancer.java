package com.github.vlmap.spring.loadbalancer.core;


import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 不要把这个类实例交给Spring 处理，
 */

public class GrayLoadBalancer implements ILoadBalancer {

    private ILoadBalancer target;
    private ServerListMetadataProvider metadataProvider;

    public GrayLoadBalancer(ILoadBalancer target, ServerListMetadataProvider metadataProvider) {
        this.target = target;

        this.metadataProvider = metadataProvider;
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


    protected List<Server> processServers(List<Server> servers) {

        if (CollectionUtils.isEmpty(servers)) return servers;
        Map<Server, GrayMetedata> matadatas = metadataProvider.transform(servers);
        if (MapUtils.isEmpty(matadatas)) {
            // 如果所有节点都没配置标签，返回所有实例，
            return servers;

        }
        String tagValue = ContextManager.getRuntimeContext().get(RuntimeContext.REQUEST_TAG_REFERENCE, String.class);

        List<Server> list = new ArrayList<>(servers.size());

        if (StringUtils.isBlank(tagValue)) {
            //正常请求，使用没配置标签的实例

            for (Server server : servers) {
                GrayMetedata info = matadatas.get(server);
                if (info == null || CollectionUtils.isEmpty(info.getTags())) {
                    list.add(server);
                }

            }
            return list;

        } else {
            //灰度请求,优先返回包含标签的实例，匹配不到再返回 无标签的实例

            for (Server server : servers) {
                GrayMetedata info = matadatas.get(server);

                if (info != null && info.getTags() != null && info.getTags().contains(tagValue)) {
                    list.add(server);
                }

            }

            if (!list.isEmpty()) return list;

            //返回无标签节点实例

            for (Server server : servers) {
                GrayMetedata info = matadatas.get(server);
                if (info == null || CollectionUtils.isEmpty(info.getTags())) {
                    list.add(server);
                }


            }
            return list;


        }


    }


}
