package com.github.vlmap.spring.loadbalancer.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

public class Platform {

    private static final String SERVLET_WEB_APPLICATION_CLASS = "org.springframework.web.context.support.GenericWebApplicationContext";

    private static final String REACTIVE_WEB_APPLICATION_CLASS = "org.springframework.web.reactive.HandlerResult";

    private static final String HYSTRIX_REQUEST_VARIABLE_CLASS = " com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable";


    private volatile static Boolean isSpringBoot1;
    private volatile static Boolean isSpringBoot2;
    private static Boolean hystrix;
    private static Boolean servlet;
    private static Boolean reactive;

    static {
        String version = org.springframework.boot.SpringBootVersion.getVersion();
        isSpringBoot1 = StringUtils.startsWith(version, "1.");
        isSpringBoot2 = StringUtils.startsWith(version, "2.");

    }

    public static boolean isHystrix() {
        if (hystrix == null) {
            if (isPresent(HYSTRIX_REQUEST_VARIABLE_CLASS, Platform.class.getClassLoader())) {
                hystrix = true;

            } else {
                hystrix = false;
            }
        }

        return hystrix;
    }


    public static boolean isSpringBoot_1() {

        return isSpringBoot1;

    }

    public static boolean isSpringBoot_2() {

        return isSpringBoot2;

    }

    public static boolean isServlet() {
        if (servlet == null) {
            if (isPresent(REACTIVE_WEB_APPLICATION_CLASS, Platform.class.getClassLoader())) {
                servlet = false;

            } else if (isPresent(SERVLET_WEB_APPLICATION_CLASS, Platform.class.getClassLoader())) {
                servlet = true;

            } else {
                servlet = false;
            }
        }

        return servlet;

    }

    public static boolean isReactive() {

        if (reactive == null) {
            if (isPresent(REACTIVE_WEB_APPLICATION_CLASS, Platform.class.getClassLoader())) {
                reactive = true;

            } else if (isPresent(SERVLET_WEB_APPLICATION_CLASS, Platform.class.getClassLoader())) {
                reactive = false;

            } else {
                reactive = false;
            }
        }

        return reactive;

    }


    public static boolean isPresent(String className, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private static Class<?> forName(String className, ClassLoader classLoader)
            throws ClassNotFoundException {
        if (classLoader != null) {
            return classLoader.loadClass(className);
        }
        return Class.forName(className);
    }

}
