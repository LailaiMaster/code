package com.lin.sleeve;

import com.lin.sleeve.mockdata.Tester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class JavaSleeveApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(JavaSleeveApplication.class);
        springApplication.addListeners((ApplicationListener<ApplicationReadyEvent>) Tester::start);
        springApplication.run(args);
    }

}
