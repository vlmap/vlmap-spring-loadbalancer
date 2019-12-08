package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import com.netflix.config.ConfigurationManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.event.EventListener;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class PropertiesListener {
    public interface ChangeListener {
        void propertyChanged(PropertyChangeEvent event);
    }

    private static Logger logger = LoggerFactory.getLogger(PropertiesListener.class);
    private ChangeListener call;
    private String name;
    private ConfigurationPropertyName propertyName = null;
    //监听以 name 开头的节点
    private boolean prefix = false;
    private boolean useString=true;
    private String id;

    public PropertiesListener(String name, ChangeListener call) {

        this(name, false, call);
    }

    public PropertiesListener(ConfigurationPropertyName name, ChangeListener call) {

        this(name, false, call);
    }

    /**
     * 字符串方式监听
     *
     * @param name
     * @param prefix
     * @param call
     */

    public PropertiesListener(String name, boolean prefix, ChangeListener call) {
        this.name = name;
        this.prefix = prefix;
        this.call = call;
        this.useString = true;
        this.id = name;

    }


    /**
     * ConfigurationPropertyName方式监听
     *
     * @param name
     * @param prefix
     * @param call
     */


    public PropertiesListener(ConfigurationPropertyName name, boolean prefix, ChangeListener call) {

        this.propertyName = name;
        this.prefix = prefix;
        this.call = call;
        this.useString = false;
        this.id = name.toString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void listener(PropertyChangeEvent event) {
        String key = event.getKey();

        if (useString) {
            if (prefix) {
                if (StringUtils.startsWith(key, name)) {
                    try {
                        if (call != null) {
                            call.propertyChanged(event);
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:" + propertyName + " error", e);
                    }
                }
            } else {
                if (StringUtils.equals(key, name)) {
                    try {
                        if (call != null) {
                            call.propertyChanged(event);
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:" + propertyName + " error", e);
                    }
                }
            }

        } else {
            ConfigurationPropertyName child = ConfigurationPropertyName.of(key);
            if (prefix) {
                if (propertyName.isAncestorOf(child) || propertyName.isParentOf(child)) {
                    try {
                        if (call != null) {
                            call.propertyChanged(event);
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:" + propertyName + " error", e);
                    }
                }
            } else {
                if (propertyName.isAncestorOf(child)) {
                    try {
                        if (call != null) {
                            call.propertyChanged(event);
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:" + propertyName + " error", e);
                    }
                }
            }
        }


    }


}
