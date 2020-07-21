package com.leng.springcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;



@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.leng.springcloud.dao")

public class Payment8001Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Payment8001Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Payment8001Application.class);
    }

}
