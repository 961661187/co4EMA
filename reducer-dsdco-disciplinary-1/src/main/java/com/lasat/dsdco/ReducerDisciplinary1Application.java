package com.lasat.dsdco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ReducerDisciplinary1Application {
    public static void main(String[] args) {
        SpringApplication.run(ReducerDisciplinary1Application.class);
    }

}
