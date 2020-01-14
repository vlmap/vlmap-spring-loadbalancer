package com.github.vlmap.spring.loadbalancer.core.cli;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GaryAttachParamater {

    private Map<String, String> headers = new LinkedMultiValueMap();


    private  Map<String, String> cookies = new LinkedMultiValueMap();

    private  Map<String, String> params = new LinkedMultiValueMap();

    private  Map<String, String> jsonpath = new LinkedMultiValueMap();
    private List<String> methods = new ArrayList<>();
    private List<String> uris = new ArrayList<>();

    private String value;
    private boolean strict = false;


    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getJsonpath() {
        return jsonpath;
    }

    public void setJsonpath(Map<String, String> jsonpath) {
        this.jsonpath = jsonpath;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
