package com.legou.trade.constants;

public abstract class PayConstants {
    /**
     * 统一的商品描述
     */
    public static final String ORDER_DESC = "乐购商城商品";
    /**
     * 统一下单支付的支付类型
     */
    public static final String UNIFIED_ORDER_TRADE_TYPE = "NATIVE";
    /**
     * 失败的CODE
     */
    public static final String FAIL = "FAIL";
    /**
     * 通信标示的KEY
     */
    public static final String RETURN_CODE_KEY = "return_code";
    /**
     * 业务标示的KEY
     */
    public static final String RESULT_CODE_KEY = "result_code";
    /**
     * 异常消息的KEY
     */
    public static final String ERROR_CODE_KEY = "err_code_des";
    /**
     * 订单编号的KEY
     */
    public static final String ORDER_NO_KEY = "out_trade_no";
    /**
     * 支付金额的KEY
     */
    public static final String TOTAL_FEE_KEY = "total_fee";
    /**
     * 支付链接的KEY
     */
    public static final String PAY_URL_KEY = "code_url";
    /**
     * 通信结果的消息
     */
    public static final String RETURN_MESSAGE_KEY = "return_msg";
}
