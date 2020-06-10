package com.legou.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.legou.user.client")
@SpringBootApplication(scanBasePackages = {"com.legou.auth","com.legou.common.advice"})
public class LgAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgAuthApplication.class,args);
    }
}
