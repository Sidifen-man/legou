package com.legou.common.constants;

public class RedisConstants {
    /**
     * 用户登录时记录TokenID的key前缀
     */
    public static final String JTI_KEY_PREFIX = "auth:login:uid:";
    /**
     * 用户登录时记录TokenID的超时时间
     */
    public static final Long TOKEN_EXPIRE_MINUTES = 30L;
}
