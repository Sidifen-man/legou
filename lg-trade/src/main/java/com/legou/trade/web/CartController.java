package com.legou.trade.web;

import com.legou.trade.entity.CartItem;
import com.legou.trade.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Void> saveCartItem(@RequestBody CartItem cartItem){
        cartService.saveCartItem(cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<CartItem>> queryCartList(){
        return ResponseEntity.ok(cartService.queryCartList());
    }

    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("id") Long skuId,@RequestParam("num") Integer num){
        cartService.updateNum(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/list")
    public ResponseEntity<Void> addCartItemList(@RequestBody List<CartItem> itemList ){
        cartService.addCartItemList(itemList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
