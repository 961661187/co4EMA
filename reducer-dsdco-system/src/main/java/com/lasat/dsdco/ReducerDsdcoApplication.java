package com.lasat.dsdco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableEurekaClient
@SpringBootApplication
@EnableAsync
public class ReducerDsdcoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReducerDsdcoApplication.class);
    }
}
