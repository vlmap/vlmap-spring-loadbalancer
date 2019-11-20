package com.github.vlmap.cloud.loadbalancer.rule;

import com.netflix.client.IClientConfigAware;
import com.netflix.client.PrimeConnections;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;

import java.util.List;

public class DelegatingLoadBalancer extends AbstractLoadBalancer implements
        PrimeConnections.PrimeConnectionListener, IClientConfigAware {

    private AbstractLoadBalancer target;
    IClientConfig clientConfig;
    public DelegatingLoadBalancer(ILoadBalancer target) {
        this.target = (AbstractLoadBalancer)target;
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
        return target.getServerList(availableOnly);
    }

    @Override
    public List<Server> getReachableServers() {
        List<Server> list = target.getReachableServers();
        return list;
    }

    @Override
    public List<Server> getAllServers() {
        return target.getAllServers();
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        this.clientConfig=clientConfig;
        if (target instanceof IClientConfigAware) {
            IClientConfigAware object = (IClientConfigAware) target;
            object.initWithNiwsConfig(clientConfig);
        }
    }

    @Override
    public void primeCompleted(Server s, Throwable lastException) {
        if (target instanceof PrimeConnections.PrimeConnectionListener) {
            PrimeConnections.PrimeConnectionListener object = (PrimeConnections.PrimeConnectionListener) target;
            object.primeCompleted(s, lastException);
        }

    }

    @Override
    public List<Server> getServerList(ServerGroup serverGroup) {
        return target.getServerList(serverGroup);
    }

    @Override
    public LoadBalancerStats getLoadBalancerStats() {
        return target.getLoadBalancerStats();
    }
}
