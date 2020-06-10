package com.legou.trade.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFormDTO {
    private Long addressId; // 收获人地址id

    private Integer paymentType;// 付款类型

    private Map<Long,Integer> carts;// 订单中商品
}
