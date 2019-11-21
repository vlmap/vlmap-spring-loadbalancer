package com.github.vlmap.spring.tools.loadbalancer.tag;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTagProcess implements TagProcess {

    @Autowired
    protected SpringToolsProperties properties;

    /**
     * 当前节点配置的TAG
     *
     * @return
     */
    protected final String currentServerTag() {

        return properties.getTagHeader();
    }

    /**
     * 获取当前请求带来的Tag
     *
     * @return
     */
    protected abstract String getRequestTag();

    public String getTag() {
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
