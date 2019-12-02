package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropChangeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.event.EventListener;


public class PropChangeListener {
    private  static    Logger logger= LoggerFactory.getLogger(PropChangeListener.class);
    private Runnable call;
    private String name;
    private ConfigurationPropertyName propertyName = null;
    //监听以 name 开头的节点
    private  boolean  prefix=false;
    private boolean useString;
    private String id;

    public PropChangeListener(String name, Runnable call) {

        this(name,false,call);
    }

    public PropChangeListener(ConfigurationPropertyName name, Runnable call) {

        this(name, false, call);
    }

    /**
     * 字符串方式监听
     * @param name
     * @param prefix
     * @param call
     */

    public PropChangeListener(String name,boolean  prefix, Runnable call) {
        this.name = name;
        this.prefix = prefix;
        this.call = call;
        this.useString=true;
        this.id=name;

    }

    /**
     * ConfigurationPropertyName方式监听
     * @param name
     * @param prefix
     * @param call
     */


    public PropChangeListener(ConfigurationPropertyName name, boolean prefix, Runnable call) {

        this.propertyName = name;
        this.prefix = prefix;
        this.call = call;
        this.useString=false;
        this.id=name.toString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @EventListener(PropChangeEvent.class)
    public void listener(PropChangeEvent event) {
        String key = event.getKey();

        if(useString){
            if(prefix){
                if(StringUtils.startsWith(key,name)) {
                    try {
                        if (call != null) {
                            call.run();
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:"+propertyName+" error",e);
                    }
                }
            }else{
                if(StringUtils.equals(key,name)) {
                    try {
                        if (call != null) {
                            call.run();
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:"+propertyName+" error",e);
                    }
                }
            }

        }else{
            ConfigurationPropertyName  child=  ConfigurationPropertyName.of(key);
            if(prefix){
                if(propertyName.isAncestorOf(child)||propertyName.isParentOf(child)) {
                    try {
                        if (call != null) {
                            call.run();
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:"+propertyName+" error",e);
                    }
                }
            }else{
                if(propertyName.isAncestorOf(child) ) {
                    try {
                        if (call != null) {
                            call.run();
                        }

                    } catch (Exception e) {
                        logger.error("listener prop:"+propertyName+" error",e);
                    }
                }
            }
        }



    }


}
