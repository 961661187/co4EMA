package com.lasat.model.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.lasat.model.sample.mapper")
public class ModelSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelSampleApplication.class);
    }
}
