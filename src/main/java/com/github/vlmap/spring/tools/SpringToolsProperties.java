package com.github.vlmap.spring.tools;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Map;


@ConfigurationProperties(prefix = "spring.tools")

public class SpringToolsProperties implements InitializingBean {
    @Autowired
    private Environment env;

    private String propertySourceName;

    private Map map = null;
    private TagRule tagRule = new TagRule();
    private PropertySource propertySource;

    public String getPropertySourceName() {
        return propertySourceName;
    }

    public void setPropertySourceName(String propertySourceName) {
        this.propertySourceName = propertySourceName;
    }

    public PropertySource getPropertySource() {
        return propertySource;
    }

    public TagRule getTagRule() {
        return tagRule;
    }

    public void setTagRule(TagRule tagRule) {
        this.tagRule = tagRule;
    }


    public String getTagHeader() {
        if (map != null) {
            return (String) map.get(tagRule.getHeaderName());
        }
        return tagRule.getHeader();
    }

    public String getTagHeaderName() {
        return tagRule.getHeaderName();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(propertySourceName)) return;
        if (env instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            PropertySource propertySource = configEnv.getPropertySources().get(propertySourceName);


            if (propertySource != null) {
                Object source = propertySource.getSource();
                if (source instanceof Map) {
                    map = (Map) source;
                }

            }
            if (map != null) {
                String header = tagRule.getHeader();
                if (header != null) {
                    map.put(tagRule.getHeaderName(), header);

                }
            }
            this.propertySource = propertySource;


        }
    }

    static class TagRule {
        private boolean enabled = false;
        private String headerName = "Loadbalancer-Tag";
        private String header;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }
    }
}
