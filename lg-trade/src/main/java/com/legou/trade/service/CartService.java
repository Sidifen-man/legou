package com.legou.trade.service;

import com.legou.trade.entity.CartItem;

import java.util.List;

public interface CartService {

    void saveCartItem(CartItem cartItem);

    List<CartItem> queryCartList();

    void updateNum(Long skuId, Integer num);

    void deleteCart(Long skuId);

    void addCartItemList(List<CartItem> itemList);
}
