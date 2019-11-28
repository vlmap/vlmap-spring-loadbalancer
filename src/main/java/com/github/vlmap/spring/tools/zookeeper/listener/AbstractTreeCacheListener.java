package com.github.vlmap.spring.tools.zookeeper.listener;


import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.env.PropertySource;

import java.nio.charset.Charset;
import java.util.Map;

public abstract class AbstractTreeCacheListener implements org.apache.curator.framework.recipes.cache.TreeCacheListener , ApplicationEventPublisherAware {
//
    protected String context;
    protected ApplicationEventPublisher publisher;

    public void setContext(String context) {
        this.context = context;
    }


    protected String sanitizeKey(String path) {
        return path.replace(this.context + "/", "").replace('/', '.');
    }

    protected String getEventDesc(TreeCacheEvent event) {
        StringBuilder out = new StringBuilder();
        out.append("type=").append(event.getType());
        out.append(", path=").append(event.getData().getPath());
        byte[] data = event.getData().getData();
        if (data != null && data.length > 0) {
            out.append(", data=").append(new String(data, Charset.forName("UTF-8")));
        }

        return out.toString();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher=applicationEventPublisher;
    }
}
