package com.hireflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Spring Boot giriş noktası; @EnableScheduling form süre doldurma işini açar
@SpringBootApplication
@EnableScheduling
public class HireflowBackendApplication {

    // Uygulamayı başlatır (tomcat, JPA, security, scheduler)
    public static void main(String[] args) {
        SpringApplication.run(HireflowBackendApplication.class, args);
    }

}
