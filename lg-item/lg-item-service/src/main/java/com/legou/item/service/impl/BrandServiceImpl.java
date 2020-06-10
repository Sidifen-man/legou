package com.legou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.dto.PageDTO;
import com.legou.common.exception.LgException;
import com.legou.item.dto.BrandDTO;
import com.legou.item.entity.Brand;
import com.legou.item.entity.CategoryBrand;
import com.legou.item.mapper.BrandMapper;
import com.legou.item.service.BrandService;
import com.legou.item.service.CategoryBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper,Brand> implements BrandService {
    private final CategoryBrandService categoryBrandService;

    public BrandServiceImpl(CategoryBrandService categoryBrandService) {
        this.categoryBrandService = categoryBrandService;
    }

    @Override
    public PageDTO<BrandDTO> queryBrandByPage(Integer page, Integer rows, String key) {
        page = Math.max(page, 1);
        rows = Math.max(rows, 5);
        Page<Brand> result = query().like(StringUtils.isNotBlank(key), "name", key).or()
                .eq(StringUtils.isNotBlank(key), "letter", key)
                .page(new Page<>(page, rows));
        long total = result.getTotal();
        long pages = result.getPages();
        List<Brand> records = result.getRecords();
        return new PageDTO<>(total,pages,BrandDTO.convertEntityList(records));
    }

    @Override
    public List<BrandDTO> queryBrandByCategory(Long id) {
        List<Brand> list = getBaseMapper().queryByCategoryId(id);
        return BrandDTO.convertEntityList(list);
    }

    @Transactional
    @Override
    public void saveBrand(BrandDTO brandDTO) {
        Brand brand = brandDTO.toEntity(Brand.class);
        save(brand);
        List<CategoryBrand> collect = brandDTO.getCategoryIds().stream().map(id -> CategoryBrand.of(id, brand.getId()))
                .collect(Collectors.toList());
        categoryBrandService.saveBatch(collect);
    }

    @Transactional
    @Override
    public void updateBrand(BrandDTO brandDTO) {
        boolean update = updateById(brandDTO.toEntity(Brand.class));
        if (!update){
            throw new LgException(500,"更新失败");
        }
        update = categoryBrandService.remove(
                new QueryWrapper<CategoryBrand>().eq("brand_id",brandDTO.getId())
        );
        if (!update){
            throw new LgException(500,"更新品牌失败，删除中间表数据出错");
        }
        List<CategoryBrand> collect = brandDTO.getCategoryIds().stream().map(id -> CategoryBrand.of(id, brandDTO.getId()))
                .collect(Collectors.toList());
        categoryBrandService.saveBatch(collect);
    }
}
