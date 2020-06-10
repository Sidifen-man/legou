package com.legou.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.trade.dao.OrderFormDTO;
import com.legou.trade.entity.Order;

import java.util.Map;

public interface OrderService extends IService<Order> {
    Long createOrder(OrderFormDTO orderFormDTO);

    String getPayUrl(Long orderId);

    void handleNotify(Map<String, String> data);

    Integer queryOrderState(Long orderId);

    Integer queryOrderStatusById(Long id);

    void evictOrderIfNecessary(Long orderId);
}
