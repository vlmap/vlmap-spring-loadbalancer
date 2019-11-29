package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.event.EventListener;


public class PropChangeListener {
    Logger logger= LoggerFactory.getLogger(PropChangeListener.class);
    private Runnable call;
    private ConfigurationPropertyName propertyName = null;
    private String  prefix;
    public PropChangeListener(String prefix, Runnable call) {
        this.prefix=prefix;
        this.propertyName = ConfigurationPropertyName.of(prefix);

        this.call = call;
    }

    @EventListener(PropChangeEvent.class)
    public void listener(PropChangeEvent event) {
        String key = event.getKey();

        if(propertyName.isParentOf(ConfigurationPropertyName.of(key))) {
            try {
                if (call != null) {
                    call.run();
                }

            } catch (Exception e) {
                logger.error("listener prop:"+prefix+" error",e);
            }
        }

    }


}
