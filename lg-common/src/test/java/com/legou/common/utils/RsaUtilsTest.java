package com.legou.common.utils;


import com.legou.common.entity.Payload;
import com.legou.common.entity.UserInfo;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;


public class RsaUtilsTest {
    private String privateFilePath = "D:\\Tools\\ssh\\id_rsa.prv";
    private String publicFilePath = "D:\\Tools\\ssh\\id_rsa.pub";


    @Test
    public void getPublicKey() {
    }

    @Test
    public void getPrivateKey() {
    }

    @Test
    public void generateKey() throws Exception {
        RsaUtils.generateKey(publicFilePath,privateFilePath,"guanda");
    }

    @Test
    public void testJWT() throws Exception {
        // 获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
        // 生成token
        String token = JwtUtils.generateTokenExpireInSeconds(new UserInfo(1L, "Jack"), privateKey, 5);
        System.out.println("token = " + token);

        // 获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
        // 解析token
        Payload<UserInfo> info = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);

        System.out.println("info.getExpiration() = " + info.getExpiration());
        System.out.println("info.getUserInfo() = " + info.getUserInfo());
        System.out.println("info.getId() = " + info.getId());
    }
}
