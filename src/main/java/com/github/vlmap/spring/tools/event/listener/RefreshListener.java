package com.github.vlmap.spring.tools.event.listener;

import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;

public class RefreshListener implements ApplicationEventPublisherAware {
    private static final String REFRESH_APPLICATION = "spring.application.refresh";
    protected ApplicationEventPublisher publisher;

    @EventListener(PropertyChangeEvent.class)
    public void listener(PropertyChangeEvent event) {
        String key = event.getKey();
        String value = event.getValue();
        if (StringUtils.equals(REFRESH_APPLICATION, key)) {
            if (BooleanUtils.toBoolean(value)) {
                this.publisher.publishEvent(new RefreshEvent(this, event, event.getEventDesc()));
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.publisher = applicationEventPublisher;
    }
}
