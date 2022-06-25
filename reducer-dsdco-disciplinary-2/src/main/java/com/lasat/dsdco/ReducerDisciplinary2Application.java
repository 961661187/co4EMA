package com.lasat.dsdco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ReducerDisciplinary2Application {
    public static void main(String[] args) {
        SpringApplication.run(ReducerDisciplinary2Application.class);
    }
}
