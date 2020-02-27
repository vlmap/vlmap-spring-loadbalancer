package com.github.vlmap.spring.loadbalancer.util;

import java.util.Set;

/**
 * 灰度路由，服务配置
 */
//@ConfigurationProperties(prefix = "MICRO-CLOUD-SERVER.ribbon")

public class TagOfServers {
    private String id;
    private Set<String> tags;

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