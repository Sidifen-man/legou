package com.legou.trade.interceptors;

import com.legou.common.entity.Payload;
import com.legou.common.entity.UserInfo;
import com.legou.common.exception.LgException;
import com.legou.common.utils.CookieUtils;
import com.legou.common.utils.JwtUtils;
import com.legou.trade.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, "LG_TOKEN");
        //获取用户信息
        try {
            Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(token, UserInfo.class);
            UserInfo userInfo = infoFromToken.getUserInfo();
            //存放用户信息
            UserHolder.setUser(userInfo.getId());
            return true;
        } catch (UnsupportedEncodingException e) {
            throw new LgException(500,"用户信息不存在");
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
