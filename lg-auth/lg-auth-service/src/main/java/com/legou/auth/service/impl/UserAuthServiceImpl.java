package com.legou.auth.service.impl;

import com.legou.auth.config.JwtProperties;
import com.legou.auth.service.UserAuthService;
import com.legou.common.constants.RedisConstants;
import com.legou.common.entity.Payload;
import com.legou.common.entity.UserInfo;
import com.legou.common.exception.LgException;
import com.legou.common.utils.CookieUtils;
import com.legou.common.utils.JwtUtils;
import com.legou.user.client.UserClient;
import com.legou.user.dto.UserDTO;
import feign.FeignException;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    private final UserClient userClient;
    private final JwtProperties properties;
    private final StringRedisTemplate redisTemplate;

    public UserAuthServiceImpl(UserClient userClient, JwtProperties properties, StringRedisTemplate redisTemplate) {
        this.userClient = userClient;
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void login(String username, String password, HttpServletResponse response) {
        try {
            // 1.查询用户
            UserDTO user = userClient.queryUserByPhoneAndPassword(username, password);
            // 2.判断查询结果
            if (user == null){
                throw new LgException(400, "用户名或密码错误");
            }
            // 3.生成token
            String jti = JwtUtils.createJTI();
            String token = JwtUtils.generateTokenWithJTI(new UserInfo(user.getId(), user.getUsername()), jti,properties.getPrivateKey());
            // 4.记录在redis，并设置过期时间
            redisTemplate.opsForValue().set(RedisConstants.JTI_KEY_PREFIX + user.getId(),jti,RedisConstants.TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
            // 5.写入cookie
            writeCookie(response, token);
        } catch (FeignException e) {
            throw new LgException(e.status(), e.contentUTF8(), e);
        }

    }

    @Override
    public String verifyUser(HttpServletRequest request) {
        // 1.服务端获取cookie中的token
        String lg_token = CookieUtils.getCookieValue(request, "LG_TOKEN");
        if (StringUtils.isBlank(lg_token)){
            throw new LgException(400, "未登录或者登录超时!");
        }
        // 2.通过公钥验证token是否有效
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(lg_token, properties.getPublicKey(), UserInfo.class);
        } catch (JwtException e) {
            throw new LgException(400,"用户未登录或登录超时！");
        }
        // 3.如果有效，返回用户信息
        UserInfo userInfo = payload.getUserInfo();
        if (userInfo == null){
            throw new LgException(400, "用户未登录或登录超时！");
        }
        //4. 校验JTI
        String redisJti = redisTemplate.opsForValue().get(RedisConstants.JTI_KEY_PREFIX + userInfo.getId());
        String Jti = payload.getId();
        if(!StringUtils.equals(Jti,redisJti)){
            throw new LgException(400,"token无效");
        }
        return userInfo.getUsername();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, "LG_TOKEN");
        if(token==null){
            return;
        }
        //解析并验证token
        try {
            Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(token, properties.getPublicKey(), UserInfo.class);
            //删除token
            redisTemplate.delete(RedisConstants.JTI_KEY_PREFIX+infoFromToken.getId() );
            //删除cookie
            CookieUtils.deleteCookie("LG_TOKEN","legou.com",response);
            //原生写法
//            Cookie lg_token = new Cookie("LG_TOKEN", "1");
//            lg_token.setDomain("legou.com");
//            lg_token.setPath("/");
//            lg_token.setMaxAge(0);
        } catch (Exception e) {
            return;
        }


    }

    private void writeCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("LG_TOKEN", token);
        // cookie的作用域
        cookie.setDomain("legou.com");
        // 是否禁止JS操作cookie，避免XSS攻击
        cookie.setHttpOnly(true);
        // cookie有效期，-1就是跟随当前会话，浏览器关闭就消失
        cookie.setMaxAge(-1);
        // cookie作用的路径，/代表一切路径
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
