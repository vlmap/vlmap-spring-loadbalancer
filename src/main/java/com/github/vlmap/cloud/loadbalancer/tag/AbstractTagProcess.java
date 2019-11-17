package com.github.vlmap.cloud.loadbalancer.tag;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import javax.annotation.PostConstruct;

public abstract class AbstractTagProcess implements TagProcess {
    @Autowired
    private ApplicationContext context;
    protected PropertySource propertySource;
    protected String defaultLoadbalancerTag;

    @PostConstruct
    public void init() {

        defaultLoadbalancerTag = context.getBean(Environment.class).getProperty(LOADBALANCER_TAG);
        if(context.containsBean("propertySource")){
            propertySource = context.getBean(PropertySource.class);
        }

    }
    /**
     * 当前节点配置的TAG
     * @return
     */
    protected final String currentServerTag() {

        if (propertySource != null) {
            Object tag = propertySource.getProperty(LOADBALANCER_TAG);
            if (tag != null) return tag.toString();
        }
        return defaultLoadbalancerTag;
    }

    /**
     * 获取当前请求带来的Tag
     * @return
     */
    protected abstract String getRequestTag();
    public   String getTag(){
        String loadbalancerTag = currentServerTag();
        String requestTag = getRequestTag();
        String tag = null;
        if (StringUtils.isNotBlank(requestTag)) {
            tag = requestTag;
        } else if (StringUtils.isNotBlank(loadbalancerTag)) {
            tag = loadbalancerTag;
        }

        return tag;
    }

}
