package com.github.vlmap.spring.tools.loadbalancer;

import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.Set;

public class RibbonClientRefresh {
    private ContextRefresher contextRefresher;

    public RibbonClientRefresh(ContextRefresher contextRefresher) {
        this.contextRefresher = contextRefresher;
    }

    @EventListener
    public void listener(){

    }
    public Collection<String> refresh() {
        Set<String> keys = this.contextRefresher.refresh();
        return keys;
    }

}
