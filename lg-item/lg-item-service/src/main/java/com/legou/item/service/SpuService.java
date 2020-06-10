package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.common.dto.PageDTO;
import com.legou.item.dto.SpuDTO;
import com.legou.item.entity.Spu;


public interface SpuService extends IService<Spu> {
    PageDTO<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id);

    void saveGoods(SpuDTO spuDTO);

    void updateSpuSaleable(Long id, Boolean saleable);

    SpuDTO queryGoodsById(Long id);

    void updateGoods(SpuDTO spuDTO);
}
