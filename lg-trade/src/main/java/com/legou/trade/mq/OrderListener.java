package com.legou.trade.mq;

import com.legou.trade.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.legou.common.constants.MQConstants.QueueConstants.EVICT_ORDER_QUEUE;

@Slf4j
@Component
public class OrderListener {
    private final OrderService orderService;

    public OrderListener(OrderService orderService) {
        this.orderService = orderService;
    }
    /**
     * 监听清理订单的消息
     * @param orderId 订单id
     */
    @RabbitListener(queues = EVICT_ORDER_QUEUE)
    public void listenOrderMessage(Long orderId){
        if (orderId != null){
            log.info("接收到订单任务,订单id:{}",orderId);
            orderService.evictOrderIfNecessary(orderId);
        }
    }
}
