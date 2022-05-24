package com.lasat.model.proxy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableEurekaClient
@EnableAsync
@SpringBootApplication
@EnableFeignClients(basePackages = "com.lasat.model.proxy.feign")
@MapperScan("com.lasat.model.proxy.mapper")
public class ModelProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelProxyApplication.class);
    }
}
