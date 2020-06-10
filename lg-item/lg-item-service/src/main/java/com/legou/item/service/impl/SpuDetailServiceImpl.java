package com.legou.item.service.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.common.utils.JsonUtils;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.Spu;
import com.legou.item.entity.SpuDetail;
import com.legou.item.mapper.SpuDetailMapper;
import com.legou.item.mapper.SpuMapper;
import com.legou.item.service.SpecParamService;
import com.legou.item.service.SpuDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpuDetailServiceImpl extends ServiceImpl<SpuDetailMapper, SpuDetail> implements SpuDetailService {
    private final SpuMapper spuMapper;
    private final SpecParamService specParamService;

    public SpuDetailServiceImpl(SpuMapper spuMapper, SpecParamService specParamService) {
        this.spuMapper = spuMapper;
        this.specParamService = specParamService;
    }

    @Override
    public List<SpecParamDTO> querySpecValues(Long id, Boolean searching) {
        //1.查询spu，获取其中的商品分类id
        Spu spu = spuMapper.selectById(id);
        if (spu == null){
            throw new LgException(404,"商品不存在");
        }
        //2.查询规格参数，根据商品分类id
        List<SpecParamDTO> paramDTOList = specParamService.queryParams(spu.getCid3(), null, searching);
        if (CollectionUtils.isEmpty(paramDTOList)){
            throw new LgException(404,"商品规格不存在！");
        }
        //3.查询商品详情,获取其中的过个参数值
        SpuDetail spuDetail = getById(id);
        String specification = spuDetail.getSpecification();
        //4.把json转换成map
        Map<Long, Object> longObjectMap = JsonUtils.toMap(specification, Long.class, Object.class);
        //5.把规格参数key和规格参数value配对
        for (SpecParamDTO param : paramDTOList
             ) {
            Long id1 = param.getId();
            Object value = longObjectMap.get(id1);
            param.setValue(value);
        }
        return paramDTOList;
    }
}
