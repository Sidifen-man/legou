package com.legou.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients({"com.legou.item.client","com.legou.user.client"})
@MapperScan("com.legou.trade.mapper")
@SpringBootApplication(scanBasePackages = {"com.legou.trade","com.legou.common.advice"})
public class LgTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LgTradeApplication.class,args);
    }
}
