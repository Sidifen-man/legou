package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.item.entity.Sku;

import java.util.Map;

public interface SkuService extends IService<Sku> {
    void deductStock(Map<Long, Integer> cartMap);

    void addStock(Map<Long, Integer> cartMap);
}
