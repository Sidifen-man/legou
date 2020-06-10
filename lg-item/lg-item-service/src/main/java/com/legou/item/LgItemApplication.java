package com.legou.item;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.legou.item.mapper")
@SpringBootApplication(scanBasePackages = {"com.legou.item","com.legou.common.advice"})
public class LgItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgItemApplication.class,args);
    }
}
