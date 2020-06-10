package com.legou.page.service.impl;

import com.legou.common.utils.BeanHelper;
import com.legou.common.utils.JsonUtils;
import com.legou.item.client.ItemClient;
import com.legou.item.dto.*;
import com.legou.page.dto.SpecGroupNameDTO;
import com.legou.page.dto.SpecParamNameDTO;
import com.legou.page.service.GoodsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsPageServiceImpl implements GoodsPageService {

    private final ItemClient itemClient;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX_SPU = "page:spu:id:";
    private static final String KEY_PREFIX_SKU = "page:sku:id:";
    private static final String KEY_PREFIX_DETAIL = "page:detail:id:";
    private static final String KEY_PREFIX_CATEGORY = "page:category:id:";
    private static final String KEY_PREFIX_BRAND = "page:brand:id:";
    private static final String KEY_PREFIX_SPEC = "page:spec:id:";


    public GoodsPageServiceImpl(ItemClient itemClient, StringRedisTemplate redisTemplate) {
        this.itemClient = itemClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String loadSpuData(Long spuId) {
        //查询出结果
        SpuDTO spuDTO = itemClient.querySpuById(spuId);
        //声明集合，组装数据
        Map<String, Object> map = new HashMap<>();
        map.put("id",spuDTO.getId());
        map.put("name",spuDTO.getName());
        map.put("brandId",spuDTO.getBrandId());
        map.put("cid1",spuDTO.getCid1());
        map.put("cid2",spuDTO.getCid2());
        map.put("cid3",spuDTO.getCid3());
        //序列化数据为json
        String json = JsonUtils.toJson(map);
        //存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_SPU + spuId,json);
        return json;
    }

    @Override
    public String loadSpuDetailData(Long spuId) {
        //查询出结果
        SpuDetailDTO spuDetailDTO = itemClient.querySpuDetailById(spuId);
        //组装数据
        //序列化
        String json = JsonUtils.toJson(spuDetailDTO);
        //存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_DETAIL + spuId,json);
        return json;
    }

    @Override
    public String loadSkuListData(Long spuId) {
        //查询出结果
        List<SkuDTO> skuList = itemClient.querySkuBySpuId(spuId);
        //组装数据
        //序列化
        String json = JsonUtils.toJson(skuList);
        //存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_SKU + spuId,json);
        return json;
    }

    /**
     * 把Sku从Redis删除
     *
     * @param spuId
     */
    @Override
    public Boolean deleteSku(Long spuId) {
        return redisTemplate.delete(KEY_PREFIX_SKU + spuId);
    }

    @Override
    public String loadCategoriesData(List<Long> ids) {
        //查询出结果
        List<CategoryDTO> categoryDTOS = itemClient.queryCategoryByList(ids);
        //组装数据
        List<Map<String, Object>> collect = categoryDTOS.stream().map(categoryDTO -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", categoryDTO.getId());
            map.put("name", categoryDTO.getName());
            return map;
        }).collect(Collectors.toList());
        //序列化
        String json = JsonUtils.toJson(collect);
        //存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_CATEGORY + ids.get(ids.size()-1),json);
        return json;
    }

    @Override
    public String loadBrandData(Long brandId) {
        //查询出结果
        BrandDTO brandDTO = itemClient.queryBrandById(brandId);
        //组装数据
        Map<String, Object> map = new HashMap<>();
        map.put("id",brandDTO.getId());
        map.put("name",brandDTO.getName());
        //序列化
        String json = JsonUtils.toJson(map);
        //存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX_BRAND + brandId,json);
        return json;
    }

    @Override
    public String loadSpecData(Long categoryId) {
        //查询出结果
        List<SpecGroupDTO> specGroupDTOS = itemClient.querySpecList(categoryId);
        //组装数据
        List<SpecGroupNameDTO> list = new ArrayList<>();
        for (SpecGroupDTO specGroupDTO:specGroupDTOS
             ) {
            SpecGroupNameDTO specGroupNameDTO = new SpecGroupNameDTO();
            specGroupNameDTO.setName(specGroupDTO.getName());
            List<SpecParamDTO> params = specGroupDTO.getParams();
            List<SpecParamNameDTO> nameList = BeanHelper.copyWithCollection(params, SpecParamNameDTO.class);
            specGroupNameDTO.setParams(nameList);
            list.add(specGroupNameDTO);
        }
        //序列化
        String json = JsonUtils.toJson(list);
        redisTemplate.opsForValue().set(KEY_PREFIX_SPEC + categoryId,json);
        return json;
    }
}
