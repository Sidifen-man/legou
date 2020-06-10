package com.legou.trade.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    INIT(1, "初始化，未付款"),
    PAY_UP(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    CONFIRMED(4, "已确认,未评价"),
    CLOSED(5, "已关闭"),
    RATED(6, "已评价，交易结束");

    /**
     * 枚举值，对应订单状态
     */
    @EnumValue
    @JsonValue
    private Integer value;
    private String msg;

    OrderStatus(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer getValue(){
        return this.value;
    }

    public String getMsg(){
        return msg;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
