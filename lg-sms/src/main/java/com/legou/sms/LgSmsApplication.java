package com.legou.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.legou.sms","com.legou.common.advice"})
public class LgSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgSmsApplication.class,args);
    }
}
