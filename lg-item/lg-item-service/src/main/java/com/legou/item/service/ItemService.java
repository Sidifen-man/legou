package com.legou.item.service;

import com.legou.common.exception.LgException;
import com.legou.item.entity.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {
    public Item saveItem(Item item){
        if(item.getPrice()==null){
            throw new LgException(400,"价格不能为空！");
        }
        if (item.getName()==null){
            throw new LgException(400,"名称不能为空！");
        }
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}
