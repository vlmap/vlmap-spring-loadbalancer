package com.github.vlmap.spring.loadbalancer;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;


@ConfigurationProperties(prefix = "vlmap.spring.loadbalancer")
@RefreshScope
public class GrayLoadBalancerProperties {

    private boolean enabled = true;
    private String headerName = "Loadbalancer-Tag";


    private Strict strict = new Strict();


    private Feign feign = new Feign();
    private RestTemplate restTemplate = new RestTemplate();
    private WebClient webClient = new WebClient();
    private Controller controller = new Controller();
    private Attach attach = new Attach();


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

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

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


    public Strict getStrict() {
        return strict;
    }

    public void setStrict(Strict strict) {
        this.strict = strict;
    }

    public Attach getAttach() {
        return attach;
    }

    public void setAttach(Attach attach) {
        this.attach = attach;
    }

    static public class Feign {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class RestTemplate {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class WebClient {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }


    /**
     * 严格模式
     */
    static public class Strict {
        /**
         * 启用兼容模式。如果不启用，正常请求路由到灰度节点（非网关服务）拒绝响应
         */
        private boolean enabled = true;
        private int code = 403;
        private String message = "Forbidden";

        private StrictIgnore ignore = new StrictIgnore();


        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public StrictIgnore getIgnore() {
            return ignore;
        }

        public void setIgnore(StrictIgnore ignore) {
            this.ignore = ignore;
        }
    }

    static public class StrictIgnore {


        private StrictDefaultIgnore Default = new StrictDefaultIgnore();


        public StrictDefaultIgnore getDefault() {
            return Default;
        }

        public void setDefault(StrictDefaultIgnore aDefault) {
            Default = aDefault;
        }

        private List<String> path;


        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }
    }

    static public class StrictDefaultIgnore {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class Controller {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    static public class Attach {
        private boolean enabled = true;
        List<String> commands;
        List<MediaType> cacheBodyContentType = Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_FORM_URLENCODED
        );

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<MediaType> getCacheBodyContentType() {
            return cacheBodyContentType;
        }

        public void setCacheBodyContentType(List<MediaType> cacheBodyContentType) {
            this.cacheBodyContentType = cacheBodyContentType;
        }

        public List<String> getCommands() {
            return commands;
        }

        public void setCommands(List<String> commands) {
            this.commands = commands;
        }
    }
}
