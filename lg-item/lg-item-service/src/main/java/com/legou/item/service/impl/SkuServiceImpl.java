package com.legou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.item.entity.Sku;
import com.legou.item.mapper.SkuMapper;
import com.legou.item.service.SkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
    private static final String DEDUCT_STOCK_STATEMENT = "com.legou.item.mapper.SkuMapper.deductStock";
    private static final String ADD_STOCK_STATEMENT = "com.legou.item.mapper.SkuMapper.addStock";
    @Override
    @Transactional
    public void deductStock(Map<Long, Integer> cartMap) {
        executeBatch(sqlSession -> {
            for (Map.Entry<Long,Integer> cart : cartMap.entrySet()) {
                Map<String , Object> parm = new HashMap<>();
                parm.put("id",cart.getKey());
                parm.put("num",cart.getValue());
                sqlSession.update(DEDUCT_STOCK_STATEMENT,parm);
            }
            sqlSession.flushStatements();
        });

    }

    @Override
    @Transactional
    public void addStock(Map<Long, Integer> cartMap) {
        executeBatch(sqlSession -> {
            for (Map.Entry<Long,Integer> cart : cartMap.entrySet()){
                Map<String, Object> parm = new HashMap<>();
                parm.put("id",cart.getKey());
                parm.put("num",cart.getValue());
                sqlSession.update(ADD_STOCK_STATEMENT,parm);
                sqlSession.flushStatements();
            }
        });
    }
}
