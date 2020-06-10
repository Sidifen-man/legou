package com.legou.sms.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.legou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageListenerTest {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void listenVerifyCodeMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("phone","18616534760");
        map.put("code","888888");
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,VERIFY_CODE_KEY,map);
    }
}
