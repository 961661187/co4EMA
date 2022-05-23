package com.lasat.model.proxy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lasat.model.proxy.mapper")
public class ModelProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModelProxyApplication.class);
    }
}
