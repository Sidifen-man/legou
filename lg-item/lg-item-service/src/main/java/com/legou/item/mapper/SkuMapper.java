package com.legou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legou.item.entity.Sku;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.Map;

public interface SkuMapper extends BaseMapper<Sku>{
    //减库存
    @Update("UPDATE tb_sku SET stock = stock - #{num},sold = sold + #{num} WHERE id = #{id}")
    int deductStock(HashMap<Object, Object> parm);

    //恢复库存
    @Update("UPDATE tb_sku SET stock = stock + #{num},sold = sold - #{num} WHERE id = #{id}")
    int addStock(HashMap<Object, Object> parm);
}
