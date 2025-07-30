package com.bank.poc.grpcvsrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestClientApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestClientApplication.class);
        // This is crucial: prevents the client from starting a web server
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}