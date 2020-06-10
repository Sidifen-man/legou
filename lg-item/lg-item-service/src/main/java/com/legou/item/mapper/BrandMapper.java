package com.legou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Select("SELECT b.id, b.name, b.letter, b.image FROM tb_category_brand cb " +
            "INNER JOIN tb_brand b ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
}
