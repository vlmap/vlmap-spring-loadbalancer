package com.github.vlmap.spring.tools.event;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.context.ApplicationEvent;

/**
 *
 */
public class PropsChangeEvent extends ApplicationEvent {


    private String eventDesc;
    private String key;
    private String value;

    public PropsChangeEvent(Object source, String key, String value, String eventDesc) {
        super(source);

        this.eventDesc = eventDesc;
        this.key = key;
        this.value = value;
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
