package com.legou.common.constants;

/**
  *@Author Haoran·Zhao
  *@Date 2020/5/28
  *@Version 1.0
  */
public abstract class MQConstants {

    public static final class ExchangeConstants {
        /**
         * 商品服务交换机名称
         */
        public static final String ITEM_EXCHANGE_NAME = "lg.item.exchange";
        /**
         * 消息服务交换机名称
         */
        public static final String SMS_EXCHANGE_NAME = "lg.sms.exchange";
        /**
         * 订单业务的交换机
         */
        public static final String ORDER_EXCHANGE_NAME = "lg.order.exchange";
        /**
         * 私信队列交换机名称
         */
        public static final String DEAD_EXCHANGE_NAME = "lg.dead.exchange";
    }

    public static final class RoutingKeyConstants {
        /**
         * 商品上架的routing-key
         */
        public static final String ITEM_UP_KEY = "item.up";
        /**
         * 商品下架的routing-key
         */
        public static final String ITEM_DOWN_KEY = "item.down";
        /**
         * 商品下架的routing-key
         */
        public static final String VERIFY_CODE_KEY = "sms.verify.code";
        /**
         * 清理订单routing-key
         */
        public static final String EVICT_ORDER_KEY = "order.evict";
    }

    public static final class QueueConstants{
        /**
         * 搜索服务，商品上架的队列
         */
        public static final String SEARCH_ITEM_UP = "search.item.up.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String SEARCH_ITEM_DOWN = "search.item.down.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String SMS_VERIFY_CODE_QUEUE = "sms.verify.code.queue";
        /**
         * 订单私信队列名称
         */
        public static final String DEAD_ORDER_QUEUE = "lg.dead.order.queue";
        /**
         * 订单清理队列名称
         */
        public static final String EVICT_ORDER_QUEUE = "lg.evict.order.queue";

    }
}