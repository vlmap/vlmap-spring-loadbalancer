package com.github.vlmap.boot;


import org.springframework.boot.SpringApplication;


public class SpringApplicationPatch {

    public static SpringApplication patch(Class<?>... primarySources) {
        SpringApplication application = new SpringApplication(primarySources);



         application.addListeners(new InitializedEventListener());

        return application;
    }



}
