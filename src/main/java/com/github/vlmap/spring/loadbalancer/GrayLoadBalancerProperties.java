package com.github.vlmap.spring.loadbalancer;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@ConfigurationProperties(prefix = "vlmap.spring.loadbalancer")
@RefreshScope
public class GrayLoadBalancerProperties {

    private boolean enabled = true;
    private String headerName = "Loadbalancer-Tag";


    private CacheBody cacheBody = new CacheBody();

    private Attacher attacher = new Attacher();

    private Responder responder = new Responder();

    private Strict strict = new Strict();
    private Actuator actuator = new Actuator();

    private Feign feign = new Feign();
    private RestTemplate restTemplate = new RestTemplate();
    private WebClient webClient = new WebClient();
    private Controller controller = new Controller();


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

    public Attacher getAttacher() {
        return attacher;
    }

    public void setAttacher(Attacher attacher) {
        this.attacher = attacher;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    public CacheBody getCacheBody() {
        return cacheBody;
    }

    public void setCacheBody(CacheBody cacheBody) {
        this.cacheBody = cacheBody;
    }

    public Responder getResponder() {
        return responder;
    }

    public void setResponder(Responder responder) {
        this.responder = responder;
    }

    static public class Actuator {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
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
         *  如果启用，正常请求负载到灰度节点或灰度请求负载到非灰度节点验证不通过
         */
        private boolean enabled = false;
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

        private List<String> path=Collections.emptyList();


        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path =path==null?Collections.emptyList():Collections.unmodifiableList( path);
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

    /**
     * body缓存配置
     */
    static public class CacheBody {
        private boolean enabled = true;
        private long maxLength = -1;

        private List<MediaType> cacheBodyContentType = Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_FORM_URLENCODED

        );

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(long maxLength) {
            this.maxLength = maxLength;
        }

        public List<MediaType> getCacheBodyContentType() {
            return cacheBodyContentType;
        }

        public void setCacheBodyContentType(List<MediaType> cacheBodyContentType) {
            this.cacheBodyContentType = cacheBodyContentType==null?Collections.emptyList():Collections.unmodifiableList(cacheBodyContentType);
        }
    }

    static public class Attacher {
        private boolean enabled = true;
        private List<String> commands=Collections.emptyList();


        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * mirror to bean  RequestMatchParamater
         *
         * @return
         */
        public List<String> getCommands() {
            return commands;
        }

        public void setCommands(List<String> commands) {
            this.commands =  commands==null?Collections.emptyList(): Collections.unmodifiableList(commands);
        }
    }

    static public class Responder {
        private boolean enabled = true;
        private List<String> commands=Collections.emptyList();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * mirror to bean  ResponderParamater
         *
         * @return
         */
        public List<String> getCommands() {
            return commands;
        }

        public void setCommands(List<String> commands) {
            this.commands =commands==null?Collections.emptyList(): Collections.unmodifiableList(commands) ;
        }
    }
}
