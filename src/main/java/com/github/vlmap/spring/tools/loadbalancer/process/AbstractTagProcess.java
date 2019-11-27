package com.github.vlmap.spring.tools.loadbalancer.process;

import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTagProcess implements TagProcess {


    protected DynamicToolProperties properties;
    public AbstractTagProcess(DynamicToolProperties properties) {
        this.properties=properties;
    }

    @Override
    public String getTagHeaderName() {
        return properties.getTagHeaderName();
    }

    /**
     * 当前节点配置的TAG
     *
     * @return
     */
    public final String currentServerTag() {

        return properties.getTagHeader();
    }


    /**
     * 获取当前请求带来的Tag
     *
     * @return
     */
    public abstract String getRequestTag();

    public String getTag() {
        String requestTag = getRequestTag();
        String tag = null;
        if (StringUtils.isNotBlank(requestTag)) {
            tag = requestTag;
        } else {
            String loadbalancerTag = currentServerTag();
            if (StringUtils.isNotBlank(loadbalancerTag)) {
                tag = loadbalancerTag;
            }
        }
        return tag;
    }

}
