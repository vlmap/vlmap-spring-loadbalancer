package com.github.vlmap.spring.tools.zookeeper.listener;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;

import java.nio.charset.StandardCharsets;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;
import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_UPDATED;

public class ConfigTreeCacheListener extends AbstractTreeCacheListener {
    private static final String REFRESH_APPLICATION = "spring.application.refresh";


    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        TreeCacheEvent.Type eventType = event.getType();
        if (eventType == NODE_ADDED || eventType == NODE_UPDATED) {
            if (event.getData() != null) {

                String key = sanitizeKey(event.getData().getPath());

                if (StringUtils.equals(REFRESH_APPLICATION, key)) {
                    byte[] data = event.getData().getData();
                    if (ArrayUtils.isNotEmpty(data)) {
                        String value = new String(data, StandardCharsets.UTF_8);
                        Boolean boolVal = BooleanUtils.toBoolean(value);
                        if (BooleanUtils.isTrue(boolVal)) {
                            this.publisher.publishEvent(new RefreshEvent(this, event, getEventDesc(event)));
                        }
                    }

                }

            }

        }
    }
}
