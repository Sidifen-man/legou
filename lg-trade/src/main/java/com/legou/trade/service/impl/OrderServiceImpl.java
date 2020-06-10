package com.legou.trade.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.item.client.ItemClient;
import com.legou.item.dto.SkuDTO;
import com.legou.trade.constants.PayConstants;
import com.legou.trade.dao.OrderFormDTO;
import com.legou.trade.entity.Order;
import com.legou.trade.entity.OrderDetail;
import com.legou.trade.entity.OrderLogistics;
import com.legou.trade.entity.enums.OrderStatus;
import com.legou.trade.mapper.OrderMapper;
import com.legou.trade.service.OrderService;
import com.legou.trade.utils.PayHelper;
import com.legou.trade.utils.UserHolder;
import com.legou.user.client.UserClient;
import com.legou.user.dto.AddressDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.legou.common.constants.MQConstants.ExchangeConstants.ORDER_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.EVICT_ORDER_KEY;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private final OrderDetailServiceImpl detailService;
    private final OrderLogisticsServiceImpl logisticsService;
    private final ItemClient itemClient;
    private final UserClient userClient;
    private final PayHelper payHelper;
    private final AmqpTemplate amqpTemplate;

    public OrderServiceImpl(OrderDetailServiceImpl detailService, OrderLogisticsServiceImpl logisticsService, ItemClient itemClient, UserClient userClient, PayHelper payHelper, AmqpTemplate amqpTemplate) {
        this.detailService = detailService;
        this.logisticsService = logisticsService;
        this.itemClient = itemClient;
        this.userClient = userClient;
        this.payHelper = payHelper;
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    @Transactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        //写入Order
        //生成order对象，用于存储数据
        Order order = new Order();
        //用户id
        Long userId = UserHolder.getUser();
        order.setUserId(userId);
        //商品金额
        Map<Long, Integer> carts = orderFormDTO.getCarts();
        //获取所有skuid用于查询价格
        List<Long> idList = new ArrayList<>(carts.keySet());
        List<SkuDTO> skuList = itemClient.querySkuBySpuIds(idList);
        //init total = 0;
        long total = 0;
        for (SkuDTO item : skuList) {
            //获取价格
            Long price = item.getPrice();
            //获取数量
            Integer integer = carts.get(item.getId());
            //累加总金额
            total += integer * price;
        }
        order.setTotalFee(total);
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setPostFee(0L);
        order.setActualFee(total+order.getPostFee());
        order.setStatus(OrderStatus.INIT);
        //Write In MYSQL
        boolean orderSave = save(order);
        if (!orderSave){
            throw new LgException(500,"订单创建失败");
        }

        //写入orderDetail
        //定义一个集合用于封装OrderDetail，一个订单里有很多商品详情，所以用集合
        List<OrderDetail> details = new ArrayList<>();
        for (SkuDTO sku : skuList) {
            //组装OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setSpec(sku.getSpecialSpec());
            detail.setTitle(sku.getTitle());
            detail.setSkuId(sku.getId());
            detail.setPrice(sku.getPrice());
            detail.setOrderId(order.getOrderId());
            detail.setNum(carts.get(sku.getId()));
            detail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            details.add(detail);
        }
        boolean saveBatch = detailService.saveBatch(details);
        if (!saveBatch){
            throw new LgException(500,"订单创建失败");
        }
        //写入orderLogistics
        //查询收货地址
        AddressDTO address = userClient.queryAddressById(orderFormDTO.getAddressId());
        if (!Objects.equals(address.getUserId(),userId)){
            throw new LgException(500,"收货地址不统一");
        }
        //封装OrderLogistics，从address中拷贝属性到OrderLogistics里
        OrderLogistics orderLogistics = address.toEntity(OrderLogistics.class);
        orderLogistics.setOrderId(order.getOrderId());
        //写入数据库
        boolean logSave = logisticsService.save(orderLogistics);
        if (!logSave){
            throw new LgException(500,"订单创建失败");
        }
        //减库存
        try {
            itemClient.deductStock(carts);
        } catch (FeignException e) {
            throw new LgException(e.status(),e.contentUTF8());
        }
        amqpTemplate.convertAndSend(ORDER_EXCHANGE_NAME,EVICT_ORDER_KEY,order.getOrderId());
        //返回订单编号
        return order.getOrderId();
    }

    @Override
    public String getPayUrl(Long orderId) {
        // 根据id查询订单
        Order order = getById(orderId);
        //判断订单是否存在，健壮性
        if (order==null){
            throw new LgException(400,"订单编号错误，订单不存在！");
        }
        // 判断订单状态是否是未付款
        if (order.getStatus() != OrderStatus.INIT){
            // 订单已经关闭或者已经支付，无需再次获取支付链接
            throw new LgException(400, "订单已经支付或者关闭！");
        }
        //TODO 尝试读取redis中的支付url
        // 获取订单金额
        Long actualFee = order.getActualFee();
        // 统一下单，获取支付链接
        String unifiedOrder = payHelper.unifiedOrder(orderId, order.getActualFee());
        return unifiedOrder;
    }

    @Transactional
    @Override
    public void handleNotify(Map<String, String> data) {
        //1.校验签名
        payHelper.checkSignature(data);
        //2.校验returnCode
        payHelper.checkReturnCode(data);
        //3.检验resultCode
        payHelper.checkResultCode(data);
        //4.校验订单金额
          //获取订单ID
        String orderIdStr = data.get(PayConstants.ORDER_NO_KEY);
          //获取订单金额
        String totalFee = data.get(PayConstants.TOTAL_FEE_KEY);
          //健壮性
        if (StringUtils.isBlank(orderIdStr)||StringUtils.isBlank(totalFee)){
            throw new LgException(400,"订单ID或者支付金额为空！");
        }
          //查询数据库中的订单并比较
        Long aLong = Long.valueOf(orderIdStr);
        Order order = getById(aLong);
        if (!StringUtils.equals(totalFee,order.getActualFee().toString())){
            throw new LgException(400,"金额不匹配");
        }
        //5.判断订单状态,非初始状态结束
        if (order.getStatus() != OrderStatus.INIT){
            return;
        }
        //6.更新数据库中订单信息
        update().set("status",OrderStatus.PAY_UP.getValue())
                .set("pay_time",new Date())
                .eq("order_id",aLong)
                .eq("status",OrderStatus.INIT.getValue())
                .update();
        log.info("处理微信支付通知成功!{}",data);
    }

    @Override
    public Integer queryOrderState(Long orderId) {
        Order order = getById(orderId);
        if (order == null){
            throw new LgException(400,"订单不存在");
        }
        Integer status = order.getStatus().getValue();
        return status;
    }

    @Override
    public Integer queryOrderStatusById(Long id) {
        Order order = getById(id);
        if (order==null){
            throw new LgException(400,"订单不存在");
        }
        Integer value = order.getStatus().getValue();
        return value;
    }

    @Override
    @Transactional
    public void evictOrderIfNecessary(Long orderId) {
        //1.查询订单
        Order order = getById(orderId);
        if (order == null){
            return;
        }
        //2.判断订单支付状态
        if(order.getStatus() !=OrderStatus.INIT){
            //订单已处理
            return;
        }
        //3.未支付，关闭
        boolean update = update().set("status", OrderStatus.CLOSED.getValue()).set("close_time", new Date())
                .eq("order_id", orderId)
                // 通过乐观锁进一步保证幂等效果
                .eq("status", OrderStatus.INIT.getValue()).update();
        if (!update){
            // 更新失败，订单状态已经改变，无需处理
            return;
        }
        log.info("已关闭超时未支付订单：{}", orderId);
        //4.查询OrderDetail
        List<OrderDetail> details = detailService.query().eq("order_id", orderId).list();
        Map<Long, Integer> skuMap = new HashMap<>();
        // 获取商品及商品数量信息
        for (OrderDetail sku : details) {
            skuMap.put(sku.getSkuId(),sku.getNum());
        }
        //5.调用微服恢复库存
        try {
            itemClient.addStock(skuMap);
        } catch (FeignException e) {
            throw new LgException(e.status(),e.contentUTF8());
        }
    }
}
