package com.github.vlmap.spring.loadbalancer.core.platform;


import org.springframework.util.MultiValueMap;

public class SimpleRequest {


    private String method;
    private String path;
    private MultiValueMap<String, String> params;
    private MultiValueMap<String, String> headers;
    private MultiValueMap<String, String> cookies;
    private String body;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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


}