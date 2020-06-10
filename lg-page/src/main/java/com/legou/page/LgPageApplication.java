package com.legou.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.legou.page","com.legou.common.advice"})
@EnableFeignClients(basePackages = "com.legou.item.client")
public class LgPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgPageApplication.class,args);
    }
}
