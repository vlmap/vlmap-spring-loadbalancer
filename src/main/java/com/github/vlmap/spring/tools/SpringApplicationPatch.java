package com.github.vlmap.spring.tools;


import com.github.vlmap.spring.tools.boot.InitializedEventListener;
import org.springframework.boot.SpringApplication;


public class SpringApplicationPatch  {

    public static SpringApplication patch(Class<?>... primarySources) {
        SpringApplication application = new SpringApplication(primarySources);


        application.addListeners(new InitializedEventListener());

        return application;
    }


}
