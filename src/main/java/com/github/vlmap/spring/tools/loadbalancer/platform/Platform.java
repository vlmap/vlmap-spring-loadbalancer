package com.github.vlmap.spring.tools.loadbalancer.platform;

public class Platform {
    public static final String SERVLET = "servlet";
    public static final String REACTIVE = "reactive";

    private static final Platform instnce = new Platform();
    private String platform;
    private boolean isServlet;
    private boolean isReactive;

    public static Platform getInstnce() {
        return instnce;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
        isServlet = SERVLET.equals(platform);
        isReactive = REACTIVE.equals(platform);
    }

    public boolean isServlet() {
        return isServlet;
    }

    public boolean isReactive() {
        return isReactive;
    }
}
