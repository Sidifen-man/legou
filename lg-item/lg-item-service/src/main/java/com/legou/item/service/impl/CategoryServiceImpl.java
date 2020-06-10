package com.legou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.item.dto.CategoryDTO;
import com.legou.item.entity.Category;
import com.legou.item.entity.CategoryBrand;
import com.legou.item.mapper.CategoryMapper;
import com.legou.item.service.CategoryBrandService;
import com.legou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryBrandService categoryBrandService;

    @Autowired
    public CategoryServiceImpl(CategoryBrandService categoryBrandService){
        this.categoryBrandService=categoryBrandService;
    }

    @Override
    public List<CategoryDTO> queryCategoryByBrandId(Long brandId) {
        List<CategoryBrand> brand_id = categoryBrandService.query().eq("brand_id", brandId).list();
        if (CollectionUtils.isEmpty(brand_id)){
            return Collections.emptyList();
        }
        List<Long> categoryIdList = brand_id.stream().map(CategoryBrand::getCategoryId).collect(Collectors.toList());
        List<Category> categories = listByIds(categoryIdList);
        return CategoryDTO.convertEntityList(categories);
    }
}
