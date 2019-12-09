package com.github.vlmap.spring.tools;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.tools")

public class SpringToolsProperties {

    public static final String DEFAULT_TOOLS_PROPERTIES_NAME = "defaultToolsProperties";

    public static final boolean ZOOKEEPER_ENABLE = true;
    public static final boolean FEIGN_ENABLE = true;
    public static final boolean REST_TEMPLATE_ENABLE = true;
    public static final boolean WEB_CLIENT_ENABLE = true;
    public static final boolean TAG_LOADBALANCER_ENABLE = true;


    private String propertySourceName = DEFAULT_TOOLS_PROPERTIES_NAME;


    private TagLoadbalancer tagLoadbalancer = new TagLoadbalancer();

    private Zookeeper zookeeper = new Zookeeper();

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getPropertySourceName() {
        return propertySourceName;
    }

    public void setPropertySourceName(String propertySourceName) {
        this.propertySourceName = propertySourceName;
    }


    public TagLoadbalancer getTagLoadbalancer() {
        return tagLoadbalancer;
    }

    public void setTagLoadbalancer(TagLoadbalancer tagLoadbalancer) {
        this.tagLoadbalancer = tagLoadbalancer;
    }


    public String getTagHeaderName() {
        return tagLoadbalancer.getHeaderName();
    }


    static public class Zookeeper {
        private boolean enabled = ZOOKEEPER_ENABLE;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class Feign {
        private boolean enabled = FEIGN_ENABLE;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class RestTemplate {
        private boolean enabled = REST_TEMPLATE_ENABLE;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class WebClient {
        private boolean enabled = WEB_CLIENT_ENABLE;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class TagLoadbalancer {
        private boolean enabled = TAG_LOADBALANCER_ENABLE;
        private String headerName = "Loadbalancer-Tag";
        /**
         * 只在 gateway,zuul 网关才会用到
         */
        private String header;
        private Feign feign = new Feign();
        private RestTemplate restTemplate = new RestTemplate();
        private WebClient webClient = new WebClient();

        public WebClient getWebClient() {
            return webClient;
        }

        public void setWebClient(WebClient webClient) {
            this.webClient = webClient;
        }

        public RestTemplate getRestTemplate() {
            return restTemplate;
        }

        public void setRestTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public Feign getFeign() {
            return feign;
        }

        public void setFeign(Feign feign) {
            this.feign = feign;
        }

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
