package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.common.dto.PageDTO;
import com.legou.item.dto.BrandDTO;
import com.legou.item.entity.Brand;

import java.util.List;

public interface BrandService extends IService<Brand> {
    PageDTO<BrandDTO> queryBrandByPage(Integer page, Integer rows, String key);

    List<BrandDTO> queryBrandByCategory(Long id);

    void saveBrand(BrandDTO brandDTO);

    void updateBrand(BrandDTO brandDTO);
}
