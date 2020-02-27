package com.github.vlmap.spring.loadbalancer.core.platform;


import org.springframework.util.LinkedMultiValueMap;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

//RequestMatchParamater
public class RequestMatchParamater extends CommandParamater {

    //匹配后返回的值
    @NotNull
    private String value;


    //Method匹配
    private String method;
    //PATH匹配，支持ANT格式的PATH ,示例：/test/**
    private String path;

    //  HEADER匹配
    private LinkedMultiValueMap<String, String> headers;
    //HEADE正则匹配
    private LinkedMultiValueMap<String, Pattern> headersRegex;

    //COOKIE匹配
    private LinkedMultiValueMap<String, String> cookies;
    //COOKIE正则匹配
    private LinkedMultiValueMap<String, Pattern> cookiesRegex;


    //参数匹配
    private LinkedMultiValueMap<String, String> params;
    //参数正则匹配
    private LinkedMultiValueMap<String, Pattern> paramsRegex;


    //JsonPath匹配. 示例：key= $.data.el[0],value=abc
    private LinkedHashMap<String, String> jsonpath;
    //JsonPath正则匹配
    private Map<String, Pattern> jsonpathRegex;

    //body匹配
    private String body;
    //body正则匹配
    private LinkedList<Pattern> bodyRegex;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public LinkedMultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(LinkedMultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public LinkedMultiValueMap<String, Pattern> getHeadersRegex() {
        return headersRegex;
    }

    public void setHeadersRegex(LinkedMultiValueMap<String, Pattern> headersRegex) {
        this.headersRegex = headersRegex;
    }

    public LinkedMultiValueMap<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(LinkedMultiValueMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public LinkedMultiValueMap<String, Pattern> getCookiesRegex() {
        return cookiesRegex;
    }

    public void setCookiesRegex(LinkedMultiValueMap<String, Pattern> cookiesRegex) {
        this.cookiesRegex = cookiesRegex;
    }

    public LinkedMultiValueMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedMultiValueMap<String, String> params) {
        this.params = params;
    }

    public LinkedMultiValueMap<String, Pattern> getParamsRegex() {
        return paramsRegex;
    }

    public void setParamsRegex(LinkedMultiValueMap<String, Pattern> paramsRegex) {
        this.paramsRegex = paramsRegex;
    }

    public LinkedHashMap<String, String> getJsonpath() {
        return jsonpath;
    }

    public void setJsonpath(LinkedHashMap<String, String> jsonpath) {
        this.jsonpath = jsonpath;
    }

    public Map<String, Pattern> getJsonpathRegex() {
        return jsonpathRegex;
    }

    public void setJsonpathRegex(Map<String, Pattern> jsonpathRegex) {
        this.jsonpathRegex = jsonpathRegex;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LinkedList<Pattern> getBodyRegex() {
        return bodyRegex;
    }

    public void setBodyRegex(LinkedList<Pattern> bodyRegex) {
        this.bodyRegex = bodyRegex;
    }
}
