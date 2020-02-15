package com.github.vlmap.spring.loadbalancer.core.attach.cli;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class GaryAttachParamater {

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap();


    private MultiValueMap<String, String> cookies = new LinkedMultiValueMap();

    private MultiValueMap<String, String> params = new LinkedMultiValueMap();


    /**
     * key  : jsonpath
     * value :  string value
     */
    private Map<String, String> jsonpath = new LinkedMultiValueMap();


    private MultiValueMap<String, Pattern> headersRegex = new LinkedMultiValueMap();
    private MultiValueMap<String, Pattern> cookiesRegex = new LinkedMultiValueMap();

    private MultiValueMap<String, Pattern> paramsRegex = new LinkedMultiValueMap();
    private Map<String, Pattern> jsonpathRegex = new LinkedMultiValueMap();


    private String method;
    private String path;

    /**
     * 匹配后返回的值
     */
    private String value;


    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public MultiValueMap<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(MultiValueMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public MultiValueMap<String, String> getParams() {
        return params;
    }

    public void setParams(MultiValueMap<String, String> params) {
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

    public MultiValueMap<String, Pattern> getHeadersRegex() {
        return headersRegex;
    }

    public void setHeadersRegex(MultiValueMap<String, Pattern> headersRegex) {
        this.headersRegex = headersRegex;
    }

    public MultiValueMap<String, Pattern> getCookiesRegex() {
        return cookiesRegex;
    }

    public void setCookiesRegex(MultiValueMap<String, Pattern> cookiesRegex) {
        this.cookiesRegex = cookiesRegex;
    }

    public MultiValueMap<String, Pattern> getParamsRegex() {
        return paramsRegex;
    }

    public void setParamsRegex(MultiValueMap<String, Pattern> paramsRegex) {
        this.paramsRegex = paramsRegex;
    }

    public Map<String, Pattern> getJsonpathRegex() {
        return jsonpathRegex;
    }

    public void setJsonpathRegex(Map<String, Pattern> jsonpathRegex) {
        this.jsonpathRegex = jsonpathRegex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaryAttachParamater that = (GaryAttachParamater) o;
        return Objects.equals(headers, that.headers) &&
                Objects.equals(cookies, that.cookies) &&
                Objects.equals(params, that.params) &&
                Objects.equals(jsonpath, that.jsonpath) &&
                Objects.equals(headersRegex, that.headersRegex) &&
                Objects.equals(cookiesRegex, that.cookiesRegex) &&
                Objects.equals(paramsRegex, that.paramsRegex) &&
                Objects.equals(jsonpathRegex, that.jsonpathRegex) &&
                Objects.equals(method, that.method) &&
                Objects.equals(path, that.path) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, cookies, params, jsonpath, headersRegex, cookiesRegex, paramsRegex, jsonpathRegex, method, path, value);
    }

    @Override
    public String toString() {
        return "GaryAttachParamater{" +
                "headers=" + headers +
                ", cookies=" + cookies +
                ", params=" + params +
                ", jsonpath=" + jsonpath +
                ", headersPattern=" + headersRegex +
                ", cookiesPattern=" + cookiesRegex +
                ", paramsPattern=" + paramsRegex +
                ", jsonpathPattern=" + jsonpathRegex +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static class Comparator implements java.util.Comparator<GaryAttachParamater> {
        java.util.Comparator<String> comparator;

        public Comparator(java.util.Comparator<String> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(GaryAttachParamater o1, GaryAttachParamater o2) {
            return comparator.compare(ObjectUtils.toString(o1.getPath()), ObjectUtils.toString(o2.getPath()));
        }
    }
}
