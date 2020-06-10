package com.legou.sms.mq;

import com.legou.common.utils.RegexUtils;
import com.legou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.legou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.QueueConstants.SMS_VERIFY_CODE_QUEUE;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;

@Slf4j
@Component
public class MessageListener {
    private final SmsUtils smsUtils;

    public MessageListener(SmsUtils smsUtils) {
        this.smsUtils = smsUtils;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SMS_VERIFY_CODE_QUEUE, durable = "true"),
            exchange = @Exchange(name = SMS_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = VERIFY_CODE_KEY
    ))
    public void listenVerifyCodeMessage(Map<String,String> msg){
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.get("phone");
        if (!RegexUtils.isPhone(phone)){
            //手机号有误不处理
            return;
        }
        String code = msg.get("code");
        if (!RegexUtils.isCodeValid(code)){
            return;
        }
        try {
            smsUtils.sendVerifyCode(phone,code);
        } catch (Exception e) {
            log.error("短信验证码发送失败",e);
        }
    }
}
