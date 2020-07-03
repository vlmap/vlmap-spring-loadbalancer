package com.github.vlmap.spring.loadbalancer;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;


@ConfigurationProperties(prefix = "vlmap.spring.loadbalancer")
@RefreshScope
public class GrayLoadBalancerProperties {

    private boolean enabled = true;
    private String headerName = "Loadbalancer-Tag";


    private CacheBody cacheBody = new CacheBody(true);

    private Attacher attacher = new Attacher(true);

    private Responder responder = new Responder(true);

    private Strict strict = new Strict(false);
    private Enabled actuator = new Enabled(true);

    private Enabled feign = new Enabled(true);
    private Enabled restTemplate = new Enabled(true);
    private Enabled webClient = new Enabled(true);
    private Enabled controller = new Enabled(true);


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

    public Enabled getController() {
        return controller;
    }

    public void setController(Enabled controller) {
        this.controller = controller;
    }

    public Enabled getWebClient() {
        return webClient;
    }

    public void setWebClient(Enabled webClient) {
        this.webClient = webClient;
    }

    public Enabled getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(Enabled restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Enabled getFeign() {
        return feign;
    }

    public void setFeign(Enabled feign) {
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

    public Enabled getActuator() {
        return actuator;
    }

    public void setActuator(Enabled actuator) {
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

    static public class Enabled {
        private boolean enabled = true;

        public Enabled() {
        }

        public Enabled(boolean enabled) {
            this.enabled = enabled;
        }

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
    public class Strict extends Enabled {

        /**
         * 如果启用，正常请求负载到灰度节点或灰度请求负载到非灰度节点验证不通过
         */
        private int code = 403;
        private String message = "Forbidden";

        private StrictIgnore ignore = new StrictIgnore();

        public Strict() {

        }

        public Strict(boolean enabled) {
            super(enabled);
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


        private boolean enableDefault = true;

        public boolean isEnableDefault() {
            return enableDefault;
        }

        public void setEnableDefault(boolean enableDefault) {
            this.enableDefault = enableDefault;
        }

        private ArrayList<String> path = null;


        public ArrayList<String> getPath() {
            return path;
        }

        public void setPath(ArrayList<String> path) {
            this.path = path;
        }
    }



    /**
     * body缓存配置
     */
    public class CacheBody extends Enabled {
        private long maxLength = -1;

        private ArrayList<MediaType> cacheBodyContentType = new ArrayList(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_FORM_URLENCODED

        ));

        public CacheBody() {

        }

        public CacheBody(boolean enabled) {
            super(enabled);
        }


        public long getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(long maxLength) {
            this.maxLength = maxLength;
        }

        public ArrayList<MediaType> getCacheBodyContentType() {
            return cacheBodyContentType;
        }

        public void setCacheBodyContentType(ArrayList<MediaType> cacheBodyContentType) {
            this.cacheBodyContentType = cacheBodyContentType;
        }
    }

    static public class Responder extends Enabled {
        public Responder() {
        }

        public Responder(boolean enabled) {
            super(enabled);
        }

        private ArrayList<String> commands = null;


        /**
         * mirror to bean  ResponderParamater
         *
         *
         */
        public ArrayList<String> getCommands() {
            return commands;
        }

        public void setCommands(ArrayList<String> commands) {
            this.commands = commands;
        }
    }

    static public class Attacher extends Enabled {
        private ArrayList<String> commands = new ArrayList();

        public Attacher() {

        }

        public Attacher(boolean enabled) {
            super(enabled);
        }

        /**
         * mirror to bean  RequestMatchParamater
         */
        public ArrayList<String> getCommands() {
            return commands;
        }

        public void setCommands(ArrayList<String> commands) {
            this.commands = commands;
        }
    }
}
