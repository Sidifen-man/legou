package com.legou.trade.utils;


public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void setUser(Long id){ tl.set(id);}

    public static Long getUser(){
        Long aLong = tl.get();
        if (aLong == null){
            return 0L;
        }
        return aLong;
    }

    public static void removeUser(){
        tl.remove();
    }

}
