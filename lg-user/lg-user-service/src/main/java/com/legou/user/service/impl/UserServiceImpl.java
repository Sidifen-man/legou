package com.legou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.common.utils.RegexUtils;
import com.legou.user.dto.UserDTO;
import com.legou.user.entity.User;
import com.legou.user.mapper.UserMapper;
import com.legou.user.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.legou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.EVICT_ORDER_KEY;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final StringRedisTemplate stringRedisTemplate;
    private final AmqpTemplate amqpTemplate;
    private static final String KEY_PREFIX="user:send:phone:";
    private final BCryptPasswordEncoder passwordEncoder;
    public UserServiceImpl(StringRedisTemplate stringRedisTemplate, AmqpTemplate amqpTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.amqpTemplate = amqpTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean exists(String data, Integer type) {
        if (type !=1 && type != 2){
            throw new LgException(400,"请求参数有误");
        }
        Integer count = query().eq(type == 1, "username", data)
                .eq(type == 2, "phone", data)
                .count();
        return count == 1;
    }

    @Override
    public void sendCode(String phone) {
        // 1.验证手机号格式
        if (!RegexUtils.isPhone(phone)){
            throw new LgException(400,"请求参数有误");
        }
        // 2.使用Apache的工具类生成6位数字验证码
        String code = RandomStringUtils.randomNumeric(6);
        // 3.保存验证码到redis
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone,code,5, TimeUnit.MINUTES);
        // 4.发送RabbitMQ消息到lg-sms
        Map<String, String> map = new HashMap<>();
        map.put("phone","18616534760");
        map.put("code","777777");
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,EVICT_ORDER_KEY,map);
    }

    @Override
    @Transactional
    public void register(User user, String code) {
        // 1.校验验证码
        // 1.1 取出redis中的验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,cacheCode)){
            throw new LgException(400,"验证码错误");
        }
        // 2.对密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 3.写入数据库
        save(user);
    }

    @Override
    public UserDTO queryUserByPhoneAndPassword(String username, String password) {
        // 1.根据用户名查询
        User user = query().eq("username", username).one();
        // 2.判断是否存在
        if (user == null){
            throw new LgException(400,"用户名错误");
        }
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new LgException(400,"密码错误");
        }
        return new UserDTO(user);
    }
}
