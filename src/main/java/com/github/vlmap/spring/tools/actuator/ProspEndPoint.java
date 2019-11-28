package com.github.vlmap.spring.tools.actuator;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.event.PropsChangeEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Endpoint(
        id = "props"
)
public class ProspEndPoint  implements ApplicationEventPublisherAware {
    private DynamicToolProperties properties;
    protected ApplicationEventPublisher publisher;

    public ProspEndPoint(DynamicToolProperties properties) {
        this.properties = properties;
    }

    @ReadOperation
    public Response props() {

        return new Response(new TreeMap(properties.getDefaultToolsProps().getSource()));
    }
    @ReadOperation
    public Response get(@Selector String name) {
        String value=(String)properties.getDefaultToolsProps().getSource().get(name);
        if(value==null){
            return new Response(new TreeMap());

        }else{
            return new Response(Collections.singletonMap(name,value));

        }
    }
    @WriteOperation
    public Response update(@Selector String name,@Selector String value) {
        Map<String,Object> source= properties.getDefaultToolsProps().getSource();
        String oldValue=(String)source.get(name);
        value=StringUtils.defaultIfBlank(value,"");
        source.put(name, value);
        if(!StringUtils.equals(oldValue,value)){
            this.publisher.publishEvent(new PropsChangeEvent(this, name, value,""));

        }
        return props();
    }
    @DeleteOperation
    public Response delete(@Selector String name) {
        Map<String,Object> source= properties.getDefaultToolsProps().getSource();
        String oldValue=(String)source.get(name);
        source.remove(name);
        if(!StringUtils.equals(oldValue,null)){
            this.publisher.publishEvent(new PropsChangeEvent(this, name, null,""));

        }

        return props();
    }

    public static class Response {
        Map contexts;

        public Response(Map contexts) {
            this.contexts = contexts;
        }

        public Map getContexts() {
            return contexts;
        }

        public void setContexts(Map contexts) {
            this.contexts = contexts;
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher=applicationEventPublisher;
    }
}
