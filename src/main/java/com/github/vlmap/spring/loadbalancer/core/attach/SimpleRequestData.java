package com.github.vlmap.spring.loadbalancer.core.attach;

import com.jayway.jsonpath.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

public class SimpleRequestData {

    private static Logger logger = LoggerFactory.getLogger(SimpleRequestData.class);

    private String method;
    private String path;
    private MultiValueMap<String, String> params;
    private MultiValueMap<String, String> headers;
    private MultiValueMap<String, String> cookies;
    private String body;
    private String contentType;

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

                if (StringUtils.isNotBlank(body) && MediaType.parseMediaType(contentType).isCompatibleWith(MediaType.APPLICATION_JSON)) {


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

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParams(MultiValueMap<String, String> params) {
        this.params = params;
    }

    public void setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public void setCookies(MultiValueMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}