package com.github.vlmap.spring.loadbalancer.core.platform;

import org.springframework.util.LinkedMultiValueMap;

import javax.validation.constraints.NotNull;

public class ResponderParamater extends CommandParamater {


    //匹配后返回的值
    @NotNull
    private String value;


    private int code = 200;
    private LinkedMultiValueMap<String, String> headers;
    private LinkedMultiValueMap<String, String> cookies;
    private String body;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LinkedMultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(LinkedMultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public LinkedMultiValueMap<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(LinkedMultiValueMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
