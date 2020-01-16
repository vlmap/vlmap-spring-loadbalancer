package com.github.vlmap.spring.loadbalancer.core.attach.cli;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;
import java.util.Objects;

public class GaryAttachParamater {

    private Map<String, String> headers = new LinkedMultiValueMap();


    private  Map<String, String> cookies = new LinkedMultiValueMap();

    private  Map<String, String> params = new LinkedMultiValueMap();
    /**
     * key  : jsonpath
     * value :  string value
     */
    private  Map<String, String> jsonpath = new LinkedMultiValueMap();
    private String method ;
    private String path ;

    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaryAttachParamater that = (GaryAttachParamater) o;
        return Objects.equals(headers, that.headers) &&
                Objects.equals(cookies, that.cookies) &&
                Objects.equals(params, that.params) &&
                Objects.equals(jsonpath, that.jsonpath) &&
                Objects.equals(method, that.method) &&
                Objects.equals(path, that.path) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, cookies, params, jsonpath, method, path, value);
    }

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class Comparator implements java.util.Comparator<GaryAttachParamater>{
        java.util.Comparator<String> comparator;

        public Comparator(java.util.Comparator<String> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(GaryAttachParamater o1, GaryAttachParamater o2) {
            return comparator.compare( ObjectUtils.toString(o1.getPath()), ObjectUtils.toString(o2.getPath()));
        }
    }
}
