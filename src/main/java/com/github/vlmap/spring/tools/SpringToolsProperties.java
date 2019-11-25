package com.github.vlmap.spring.tools;


import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties(prefix = "spring.tools")

public class SpringToolsProperties  {


    private String propertySourceName="defaultToolsProps";




    private TagLoadBalancer tagLoadBalancer = new TagLoadBalancer();
     public String getPropertySourceName() {
        return propertySourceName;
    }

    public void setPropertySourceName(String propertySourceName) {
        this.propertySourceName = propertySourceName;
    }


    public TagLoadBalancer getTagLoadBalancer() {
        return tagLoadBalancer;
    }

    public void setTagLoadBalancer(TagLoadBalancer tagLoadBalancer) {
        this.tagLoadBalancer = tagLoadBalancer;
    }




    public String getTagHeaderName() {
        return tagLoadBalancer.getHeaderName();
    }




    static public class Feign{
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    static public class RestTemplate{
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    static public class WebClient{
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    static public class TagLoadBalancer {
        private boolean enabled = true;
        private String headerName = "Loadbalancer-Tag";
        private String header;
        private Feign feign=new Feign();
        private RestTemplate restTemplate=new RestTemplate();
        private WebClient webClient=new WebClient();

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
