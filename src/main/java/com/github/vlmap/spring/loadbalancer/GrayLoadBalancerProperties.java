package com.github.vlmap.spring.loadbalancer;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@ConfigurationProperties(prefix = "spring.tools.loadbalancer")
@RefreshScope
public class GrayLoadBalancerProperties {

    private boolean enabled = true;
    private String headerName = "Loadbalancer-Tag";






    private Strict strict = new Strict();


    private Feign feign = new Feign();
    private RestTemplate restTemplate = new RestTemplate();
    private WebClient webClient = new WebClient();


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

        private CompatibleIgnore ignore=new CompatibleIgnore();



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


        public CompatibleIgnore getIgnore() {
            return ignore;
        }

        public void setIgnore(CompatibleIgnore ignore) {
            this.ignore = ignore;
        }
    }

    static public class CompatibleIgnore {

        public final static AtomicReference<Collection<String>> DEFAULT_IGNORE_PATH =new AtomicReference<>();

        private List<String> path;
//        /**
//         * 忽略列表 AntPath格式,多个用“,”隔开
//         */
//        private String ignoreUrl;
//
//        public void setIgnoreUrl(String ignoreUrl) {
//            this.ignoreUrl = ignoreUrl;
//            if (StringUtils.isNotBlank(ignoreUrl)) {
//                String[] elements = StringUtils.split(ignoreUrl, ",");
//                Set<String> collection = new LinkedHashSet<>();
//                for (String element : elements) {
//                    if (StringUtils.isNotBlank(element)) {
//                        collection.add(element);
//                    }
//                }
//                this.ignoreUrls = new ArrayList<>(collection);
//            } else {
//                this.ignoreUrls = null;
//            }
//        }
//
//
//
//        public String getIgnoreUrl() {
//            return ignoreUrl;
//        }
//
//        public List<String> ignoreUrls() {
//            return ignoreUrls;
//        }


        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }
    }
}
