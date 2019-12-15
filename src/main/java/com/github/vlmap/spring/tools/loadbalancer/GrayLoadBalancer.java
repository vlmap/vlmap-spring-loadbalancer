package com.github.vlmap.spring.tools.loadbalancer;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.context.ContextManager;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GrayLoadBalancer implements ILoadBalancer {

    private GrayClientServer grayClientServer;
    private ILoadBalancer target;


    private SpringToolsProperties properties;

    public GrayLoadBalancer(GrayClientServer grayClientServer, SpringToolsProperties properties) {
        this.grayClientServer = grayClientServer;
        this.properties = properties;
    }




    public void setTarget(ILoadBalancer target) {
        this.target = target;
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
        String tag = ContextManager.getRuntimeContext().getTag();
        if (StringUtils.isBlank(tag)) {
            tag = properties.getGrayLoadbalancer().getHeader();

        }
        return tag;
    }

    protected List<Server> processServers(List<Server> servers) {

        Map<String, Set<String>> map = grayClientServer.getClientServerTags();
        if (map==null||map.isEmpty()) {
            return servers;             // 如果所有节点都没配标签，返回所有列表，

        }
        String tagValue = tag();
        List<Server> list = new ArrayList<>(servers.size());

        if (StringUtils.isBlank(tagValue)) {
            //无标签请求，排除包含标签的节点

            for (Server server : servers) {
                Set<String> tags = map.get(server.getId());
                if (CollectionUtils.isEmpty(tags)) {
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
            //匹配不到则返回无标签节点
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




}
