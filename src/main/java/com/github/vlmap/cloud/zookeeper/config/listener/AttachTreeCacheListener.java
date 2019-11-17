package com.github.vlmap.cloud.zookeeper.config.listener;


import com.github.vlmap.cloud.zookeeper.config.ProxyMap;
import com.github.vlmap.cloud.zookeeper.config.event.AttachRefreshEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 附加配置，配置更新时直接推送到客户端，并且配置更新不会影响 spring content
 */
public class AttachTreeCacheListener extends AbstractTreeCacheListener {

    Map<String, String> map = new HashMap<>();

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event)
            throws Exception {
        TreeCacheEvent.Type eventType = event.getType();

        if (event.getData() != null) {

            Object object = this.propertySource.getSource();
            //通过对象替换解决多线程问题，提高读效率
            if (object instanceof ProxyMap) {
                ProxyMap source = (ProxyMap) object;
                String path=event.getData().getPath();
                if(!StringUtils.equals(this.context,path)){
                    String key = sanitizeKey(path);
                    String value = null;

                    if (eventType == TreeCacheEvent.Type.NODE_ADDED || eventType == TreeCacheEvent.Type.NODE_UPDATED) {

                        byte[] data = event.getData().getData();
                        if (ArrayUtils.isNotEmpty(data)) {
                            value = new String(data, "utf-8");
                        }
                        map.put(key, value);
                    } else if (eventType == TreeCacheEvent.Type.NODE_REMOVED) {
                        map.remove(key);
                    }
                    source.setMap(new HashMap(map));
                    this.publisher.publishEvent(new AttachRefreshEvent(this,key,value, event, getEventDesc(event)));
                }

            }

        }
    }
}
