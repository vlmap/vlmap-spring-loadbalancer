package com.github.vlmap.spring.tools.zookeeper.listener;


import com.github.vlmap.spring.tools.event.PropsChangeEvent;
import com.github.vlmap.spring.tools.zookeeper.listener.AbstractTreeCacheListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;

import java.nio.charset.StandardCharsets;


/**
 * 附加配置，配置更新时直接推送到客户端，并且配置更新不会影响 spring content
 */
public class PropTreeCacheListener extends AbstractTreeCacheListener {

    private CompositePropertySource composite;
    private MapPropertySource container;
    private MapPropertySource defaultToolsProps;

    public void setDefaultToolsProps(MapPropertySource defaultToolsProps) {
        this.defaultToolsProps = defaultToolsProps;
    }

    public void setComposite(CompositePropertySource composite) {
        this.composite = composite;
    }

    public void setContainer(MapPropertySource container) {
        this.container = container;
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event)  throws Exception {
        TreeCacheEvent.Type eventType = event.getType();

        if (event.getData() != null) {



                 String path = event.getData().getPath();
                if (!StringUtils.equals(this.context, path)) {
                    String key = sanitizeKey(path);
                    String oldValue=(String)composite.getProperty(key);

                    String value = null;

                    if (eventType == TreeCacheEvent.Type.NODE_ADDED || eventType == TreeCacheEvent.Type.NODE_UPDATED) {

                        byte[] data = event.getData().getData();
                        if (ArrayUtils.isNotEmpty(data)) {
                            value = new String(data, StandardCharsets.UTF_8);
                        }

                        container.getSource().put(key, value);
                    } else if (eventType == TreeCacheEvent.Type.NODE_REMOVED) {
                        container.getSource().remove(key);
                    }
                    String newValue=(String)composite.getProperty(key);
                    if(ObjectUtils.notEqual(oldValue,newValue)){
                        String localValue=(String)defaultToolsProps.getProperty(key);
                        if(ObjectUtils.notEqual(localValue,newValue)){
                            if(newValue==null){
                                defaultToolsProps.getSource().remove(key)    ;
                            }else{
                                defaultToolsProps.getSource().put(key,newValue);
                            }
                            this.publisher.publishEvent(new PropsChangeEvent(this, key, value, event, getEventDesc(event)));

                        }
                    }

                 }



        }
    }
}
