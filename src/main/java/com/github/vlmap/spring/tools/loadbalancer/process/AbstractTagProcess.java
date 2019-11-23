package com.github.vlmap.spring.tools.loadbalancer.process;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTagProcess implements TagProcess {

    @Autowired
    protected SpringToolsProperties properties;

    /**
     * 当前节点配置的TAG
     *
     * @return
     */
    public final String currentServerTag() {

        return properties.getTagHeader();
    }

    public abstract void setTag(String tag);

    /**
     * 获取当前请求带来的Tag
     *
     * @return
     */
    public abstract String getRequestTag();

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
