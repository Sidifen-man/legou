package com.legou.trade.web;

import com.legou.trade.dao.OrderDTO;
import com.legou.trade.dao.OrderFormDTO;
import com.legou.trade.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderFormDTO orderFormDTO){
       return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderFormDTO));

    }
    @GetMapping("{id}")
    public ResponseEntity<OrderDTO> queryOrderById(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(new OrderDTO(orderService.getById(orderId)));
    }
    /**
     * 查询订单支付状态
     * @param orderId 订单id
     * @return 状态值
     */
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(orderService.queryOrderState(orderId));
    }
}
