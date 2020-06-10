package com.legou.trade.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cart_user_#{T(com.legou.trade.utils.UserHolder).getUser()}")
public class CartItem {
    private Long skuId;//商品id
    private String title;//标题
    private String image;//图片
    private Long price;//加入购物车时的价格
    private Integer num;//购买数量
    private String spec;//规格参数
}
