package com.github.vlmap.spring.tools.loadbalancer.process;

 import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;


public abstract class AbstractTagProcess implements TagProcess {


    protected SpringToolsProperties properties;
    public AbstractTagProcess(SpringToolsProperties properties) {
        this.properties=properties;
    }

    @Override
    public String getTagHeaderName() {
        return properties.getTagHeaderName();
    }






}
