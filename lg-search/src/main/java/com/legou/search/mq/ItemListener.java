package com.legou.search.mq;

import com.legou.common.constants.MQConstants;
import com.legou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.legou.common.constants.MQConstants.ExchangeConstants.ITEM_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.QueueConstants.SEARCH_ITEM_DOWN;
import static com.legou.common.constants.MQConstants.QueueConstants.SEARCH_ITEM_UP;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.ITEM_DOWN_KEY;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.ITEM_UP_KEY;

@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(name = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = ITEM_UP_KEY
    ))
    public void listenItemUp(Long spuId){
        if (spuId != null){
            //商品上架，我们新增商品到索引库
            searchService.saveGoodsById(spuId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(name = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = ITEM_DOWN_KEY
    ))
    public void listenItemDown(Long spuId){
        if (spuId != null){
            //商品下架，我们删除商品
            searchService.deleteGoodsById(spuId);
        }
    }
}
