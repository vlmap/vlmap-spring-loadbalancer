package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropChangeEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelegatePropChangeListener  {
    Map<String,PropChangeListener> listeners=new ConcurrentHashMap<>();

    @EventListener(PropChangeEvent.class)
    public void listener(PropChangeEvent event) {

        for(PropChangeListener listener:listeners.values()){
            listener.listener(event);
        }
    }

    public void addListener(PropChangeListener listener){
        if(listener==null)return;
        String id=listener.getId();
        if(id!=null){
            listeners.put(id,listener);
        }
    }

}
