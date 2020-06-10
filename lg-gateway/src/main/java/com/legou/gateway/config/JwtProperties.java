package com.legou.gateway.config;

import com.legou.common.exception.LgException;
import com.legou.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "lg.jwt")
public class JwtProperties implements InitializingBean {
    /**
     * 公钥文件地址
     */
    private String pubKeyPath;
    /**
     * 公钥对象
     */
    private PublicKey publicKey;



    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            log.info("【网关】加载公钥成功！");
        } catch (Exception e) {
            log.error("【网关】初始化公钥失败！", e);
            throw new RuntimeException(e);
        }
    }
}
