package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.event.EventListener;

import javax.validation.constraints.NotNull;


public class PropChangeListener {
    private  static    Logger logger= LoggerFactory.getLogger(PropChangeListener.class);
    private Runnable call;
    private ConfigurationPropertyName propertyName = null;
    //监听以 name 开头的节点
    boolean  prefix=false;
    private String id;
    public PropChangeListener(String name, Runnable call) {

        this(name,false,call);
    }
    public PropChangeListener(String name,boolean  prefix, Runnable call) {

        this.propertyName = ConfigurationPropertyName.of(name);

        this.call = call;
        setId(name);
    }

    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    @EventListener(PropChangeEvent.class)
    public void listener(PropChangeEvent event) {
        String key = event.getKey();

        ConfigurationPropertyName  child=  ConfigurationPropertyName.of(key);
        if(prefix){
            if(propertyName.isAncestorOf(child)||propertyName.isParentOf(child)) {
                try {
                    if (call != null) {
                        call.run();
                    }

                } catch (Exception e) {
                    logger.error("listener prop:"+id+" error",e);
                }
            }
        }else{
            if(propertyName.isAncestorOf(child) ) {
                try {
                    if (call != null) {
                        call.run();
                    }

                } catch (Exception e) {
                    logger.error("listener prop:"+id+" error",e);
                }
            }
        }


    }


}
