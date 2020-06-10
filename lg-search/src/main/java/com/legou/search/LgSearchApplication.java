package com.legou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.legou.item.client")
@SpringBootApplication(scanBasePackages = {"com.legou.search","com.legou.common.advice"})
public class LgSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgSearchApplication.class,args);
    }
}
