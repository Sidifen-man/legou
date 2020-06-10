package com.legou.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.legou.user","com.legou.common.advice"})
@MapperScan("com.legou.user.mapper")
public class LgUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgUserApplication.class,args);
    }
}
