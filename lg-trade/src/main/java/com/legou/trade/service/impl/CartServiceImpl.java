package com.legou.trade.service.impl;

import com.legou.common.exception.LgException;
import com.legou.trade.entity.CartItem;
import com.legou.trade.repository.CartRepository;
import com.legou.trade.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public void saveCartItem(CartItem cartItem) {
        //先获取购物车数据进行合并计算
        saveOrUpdateCartItem(cartItem);
        //写入db
        cartRepository.save(cartItem);

    }

    private CartItem saveOrUpdateCartItem(CartItem cartItem) {
        Optional<CartItem> optionalCartItem = cartRepository.findById(cartItem.getSkuId());
        //判断是否存在
        if (optionalCartItem.isPresent()){
            //存在记录新的商品数量
            Integer cartItemNum = cartItem.getNum();
            //获取旧的商品数量
            CartItem cartItemOld = optionalCartItem.get();
            //累加
            cartItem.setNum(cartItemNum+cartItemOld.getNum());
        }
        return cartItem;
    }

    @Override
    public List<CartItem> queryCartList() {
        List<CartItem> all = cartRepository.findAll();
        return all;
    }

    @Override
    public void updateNum(Long skuId, Integer num) {
        //查询购物车数据
        Optional<CartItem> cartItemOld = cartRepository.findById(skuId);
        if (!cartItemOld.isPresent()){
            throw new LgException(400,"商品不存在");
        }
        CartItem cartItem = cartItemOld.get();
        cartItem.setNum(num);
        cartRepository.save(cartItem);
    }

    @Override
    public void deleteCart(Long skuId) {
        cartRepository.deleteById(skuId);
    }

    @Override
    public void addCartItemList(List<CartItem> itemList) {
        List<CartItem> list = new ArrayList<>(itemList.size());
        for (CartItem item : itemList) {
            CartItem cartItem = saveOrUpdateCartItem(item);
            list.add(cartItem);
        }
        cartRepository.saveAll(list);
    }
}
