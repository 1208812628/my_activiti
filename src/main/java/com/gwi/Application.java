package com.gwi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class,org.activiti.spring.boot.SecurityAutoConfiguration.class})
@ComponentScan(value = { "com.gwi", "org.activiti.rest" })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
