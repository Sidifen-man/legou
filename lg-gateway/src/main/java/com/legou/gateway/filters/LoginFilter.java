package com.legou.gateway.filters;

import com.legou.common.constants.RedisConstants;
import com.legou.common.entity.Payload;
import com.legou.common.entity.UserInfo;
import com.legou.common.exception.LgException;
import com.legou.common.utils.JwtUtils;
import com.legou.gateway.config.FilterProperties;
import com.legou.gateway.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class LoginFilter implements GlobalFilter, Ordered {
    private final JwtProperties jwtProperties;
    private final FilterProperties filterProperties;
    private final StringRedisTemplate redisTemplate;
    public LoginFilter(JwtProperties jwtProperties, FilterProperties filterProperties, StringRedisTemplate redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.filterProperties = filterProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            // 1.获取Request对象
            ServerHttpRequest request = exchange.getRequest();
            // 2.获取cookie
            HttpCookie lg_token = request.getCookies().getFirst("LG_TOKEN");
            if (lg_token == null){
                throw new LgException(401,"未登录或状态无效!");
            }
            // 3.校验token是否有效
            String lg_tokenValue = lg_token.getValue();
            try {
                Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(lg_tokenValue, jwtProperties.getPublicKey(), UserInfo.class);
                UserInfo userInfo = infoFromToken.getUserInfo();
                String redisJTI = redisTemplate.opsForValue().get(RedisConstants.JTI_KEY_PREFIX + userInfo.getId());
                String Jti= infoFromToken.getId();
                if(!StringUtils.equals(Jti,redisJTI)){
                    throw new LgException(400,"未登录或状态无效");
                }
                //刷新token有效期
                redisTemplate.expire(RedisConstants.JTI_KEY_PREFIX+userInfo.getId(),RedisConstants.TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
            } catch (JwtException e) {
                throw new LgException(401,"未登录或状态无效");
            }

            //4.有效，放行
            return chain.filter(exchange);
        } catch (LgException e) {
            if (isAllowRequest(exchange)){
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isAllowRequest(ServerWebExchange exchange) {
        // 1.获取当前请求Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.获取请求方式和请求路径
        String psth = request.getPath().toString();
        String method = request.getMethodValue();
        // 3.遍历白名单
        for (Map.Entry<String, Set<String>> entry :filterProperties.getAllowRequests().entrySet()) {
            // 白名单路径
            String prefixKey = entry.getKey();
            // 白名单methods集合
            Set<String> values = entry.getValue();
            if (StringUtils.startsWith(psth,prefixKey)&& values.contains(method)){
                //符合白名单
                return true;
            }
        }
        //不符合白名单
        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
