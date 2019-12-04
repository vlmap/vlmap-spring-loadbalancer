package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelegatePropChangeListener {
    Map<String, PropertiesListener> listeners = new ConcurrentHashMap<>();

    @EventListener(PropertyChangeEvent.class)
    public void listener(PropertyChangeEvent event) {

        for (PropertiesListener listener : listeners.values()) {
            listener.listener(event);
        }
    }

    public void addListener(PropertiesListener listener) {
        if (listener == null) return;
        String id = listener.getId();
        if (id != null) {
            listeners.put(id, listener);
        }
    }

}
