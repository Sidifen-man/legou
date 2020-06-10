package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.item.dto.CategoryDTO;
import com.legou.item.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryDTO> queryCategoryByBrandId(Long brandId);
}
