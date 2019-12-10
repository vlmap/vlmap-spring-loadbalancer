package com.github.vlmap.spring.tools.context.event.listener;

import com.github.vlmap.spring.tools.context.event.PropertyChangeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelegatePropertiesChangeListener {
    private  Map<String, PropertiesListener> listeners = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(DelegatePropertiesChangeListener.class);

    @EventListener(PropertyChangeEvent.class)
    public void listener(PropertyChangeEvent event) {

        for (PropertiesListener listener : listeners.values()) {
            try{
                if(match(listener,event)){
                    listener.getCall().propertyChanged(event);
                }

            }catch (Exception e){
                if(logger.isErrorEnabled()){
                    logger.error("",e);
                }

            }

        }
    }
    protected  boolean match(PropertiesListener listener,PropertyChangeEvent event){
        String key = event.getKey();
        String name=listener.getName();
        boolean prefix=listener.isPrefix();

        if (prefix) {
            if (StringUtils.startsWith(key, name)) {
              return true;
            }
        } else {
            if (StringUtils.equals(key, name)) {
               return true;
            }
        }
        return  false;
    }

    public void addListener(PropertiesListener listener) {
        if (listener == null) return;
        String id = listener.getId();
        if (id != null) {
            listeners.put(id, listener);
        }
    }

}
