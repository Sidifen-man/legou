package com.legou.trade.repository;

import com.legou.trade.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartItem,Long> {
}
