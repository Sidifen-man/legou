package com.legou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.item.entity.CategoryBrand;
import com.legou.item.mapper.CategoryBrandMapper;
import com.legou.item.service.CategoryBrandService;
import org.springframework.stereotype.Service;

@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService {
}
