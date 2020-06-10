package com.legou.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping(value = "/hystrix/fallback")
    public ResponseEntity<String> fallBackController(){
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("请求超时...");
    }
}
