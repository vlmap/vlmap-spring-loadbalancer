package com.github.vlmap.cloud.zookeeper.config.event;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.context.ApplicationEvent;

/**
 *
 */
public class AttachRefreshEvent extends ApplicationEvent {

    private TreeCacheEvent event;

    private String eventDesc;
    private String key;
    private String value;
    public AttachRefreshEvent(Object source,String key,String value, TreeCacheEvent event, String eventDesc) {
        super(source);
        this.event = event;
        this.eventDesc = eventDesc;
        this.key=key;
        this.value=value;
    }

    public TreeCacheEvent getEvent() {
        return this.event;
    }

    public String getEventDesc() {
        return this.eventDesc;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
