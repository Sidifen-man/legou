package com.legou.trade.web;

import com.legou.trade.dao.PayResultDTO;
import com.legou.trade.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("pay")
public class PayController {
    private final OrderService orderService;

    public PayController(OrderService orderService) {
        this.orderService = orderService;
    }
    /**
     * 根据订单编号创建支付链接
     * @param orderId 订单编号
     * @return 支付链接
     */

    @GetMapping("/url/{id}")
    public ResponseEntity<String> getPayUrl(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(orderService.getPayUrl(orderId));
    }
    /**
     * 处理微信的异步通知
     * @param data 通知内容
     * @return 处理结果
     */
    @PostMapping(value = "/wx/notify",produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<PayResultDTO> handleWxNotify(@RequestBody Map<String,String> data){
        orderService.handleNotify(data);
        return ResponseEntity.ok(new PayResultDTO());
    }
    /**
     * 根据订单编号查询订单状态
     * @param id 通知内容
     * @return 订单状态码
     */

    @GetMapping("/status/{id}")
    public ResponseEntity<Integer> queryOrderStatusById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderStatusById(id));
    }
}
