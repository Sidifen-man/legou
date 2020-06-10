package com.legou.sms.utils;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.legou.common.exception.LgException;
import com.legou.common.utils.JsonUtils;
import com.legou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SmsUtils {
    private final IAcsClient acsClient;
    private final SmsProperties smsProperties;

    public SmsUtils(IAcsClient acsClient, SmsProperties smsProperties) {
        this.acsClient = acsClient;
        this.smsProperties = smsProperties;
    }

    /**
     * 发送短信验证码的方法
     *
     * @param phone 手机号
     * @param code  验证码
     */

    public void sendVerifyCode(String phone,String code){
        sendMessage(phone,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate(),"{\"code\":\"" + code + "\"}");
    }
    /**
     * 通用的发送短信的方法
     *
     * @param phone    手机号
     * @param signName 签名
     * @param template 模板
     * @param param    模板参数，json风格
     */
    private void sendMessage(String phone,String signName,String template,String param){
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(smsProperties.getDomain());
        request.setVersion(smsProperties.getVersion());
        request.setAction(smsProperties.getAction());
        request.putQueryParameter("RegionId", smsProperties.getRegionID());
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", template);
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            String data = response.getData();
            Map<String, String> mapData = JsonUtils.toMap(data, String.class, String.class);
            if (!"ok".equals(mapData.get("Code"))) {
                log.error("发送短信失败，手机号{}，原因：{}", phone, mapData.get("Message"));
                throw new LgException(500, "发送短信失败");
            }
            log.info("短信发送成功，手机号{}", phone);
        } catch (ServerException e) {
            log.error("发送短信失败，服务端异常", e);
            throw new LgException(500, e);
        } catch (ClientException e) {
            log.error("发送短信失败，客户端异常", e);
            throw new LgException(500, e);
        }
    }
}
