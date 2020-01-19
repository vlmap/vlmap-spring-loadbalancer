package com.github.vlmap.spring.loadbalancer.core.attach;

import com.jayway.jsonpath.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

public class SimpleRequestData {

    private static Logger logger = LoggerFactory.getLogger(SimpleRequestData.class);

    String method;
    String path;
    MultiValueMap<String, String> params;
    MultiValueMap<String, String> headers;
    MultiValueMap<String, String> cookies;
    String body;
    String contentType;

    private boolean parseJson = false;
    Object document;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Object getJsonDocument() {
        if (!parseJson) {
            parseJson = true;
            try {
                if (StringUtils.isNotBlank(body) && StringUtils.equalsIgnoreCase(contentType, "application/json")) {
                    this.document = Configuration.defaultConfiguration().jsonProvider().parse(body);
                }
            } catch (Exception e) {
                logger.error("parse json error,json:" + body);
            }
        }
        return this.document;
    }

    public MultiValueMap<String, String> getParams() {
        return params;
    }

    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public MultiValueMap<String, String> getCookies() {
        return cookies;
    }

    public String getBody() {
        return body;
    }


}