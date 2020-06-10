package com.legou.auth.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.legou.auth.config.OSSProperties;
import com.legou.auth.dto.AliOssSignatureDTO;
import com.legou.auth.service.AliAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AliAuthServiceImpl implements AliAuthService {
    private OSSProperties ossProperties;
    private OSS client;

    public AliAuthServiceImpl(OSSProperties ossProperties, OSS client) {
        this.ossProperties = ossProperties;
        this.client = client;
    }

    @Override
    public AliOssSignatureDTO getSignature() {
        try {
            // 1.计算过期时间
            long expireTime = ossProperties.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime;
            Date expiration = new Date(expireEndTime);

            // 2.设置上传策略
            PolicyConditions policyConds = new PolicyConditions();
            // 2.1.文件大小限制
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, ossProperties.getMaxFileSize());
            // 2.2.上传目录
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, ossProperties.getDir());
            // 2.3.生成策略
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            // 2.4.编码
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            // 3.生成签名
            String postSignature = client.calculatePostSignature(postPolicy);

            // 4.封装要返回的结果
            return AliOssSignatureDTO.of(ossProperties.getAccessKeyId(), ossProperties.getHost(), encodedPolicy, postSignature, expireEndTime, ossProperties.getDir());
        } catch (Exception e) {
            log.error("上传文件失败，原因：{}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败！", e);
        }
    }
}
