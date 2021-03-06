package com.legou.auth.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OSSProperties.class)
public class OSSConfig {
    @Bean
    public OSS ossClient(OSSProperties prop){
        return new OSSClientBuilder().build(prop.getEndpoint(),prop.getAccessKeyId(),prop.getAccessKeySecret());
    }
}
