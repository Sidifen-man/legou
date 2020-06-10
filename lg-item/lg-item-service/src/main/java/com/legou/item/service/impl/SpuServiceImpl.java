package com.legou.item.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.dto.PageDTO;
import com.legou.common.exception.LgException;
import com.legou.item.dto.SkuDTO;
import com.legou.item.dto.SpuDTO;
import com.legou.item.dto.SpuDetailDTO;
import com.legou.item.entity.*;
import com.legou.item.mapper.SpuMapper;
import com.legou.item.service.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.legou.common.constants.MQConstants.ExchangeConstants.ITEM_EXCHANGE_NAME;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.ITEM_DOWN_KEY;
import static com.legou.common.constants.MQConstants.RoutingKeyConstants.ITEM_UP_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements SpuService {
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final SpuDetailService spuDetailService;
    private final SkuService skuService;
    private final AmqpTemplate amqpTemplate;
    public SpuServiceImpl(BrandService brandService, CategoryService categoryService, SpuDetailService spuDetailService, SkuService skuService, AmqpTemplate amqpTemplate) {
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.spuDetailService = spuDetailService;
        this.skuService = skuService;
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public PageDTO<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id) {
        int current = Math.max(page,1);
        int size = Math.max(rows,5);
        Page<Spu> result = query().eq(saleable != null, "saleable", saleable)
                .eq(categoryId != null, "cid3", categoryId)
                .eq(brandId != null, "brand_id", brandId)
                .eq(id != null, "id", id)
                .page(new Page<>(current, size));
        long total = result.getTotal();
        long pages = result.getPages();
        List<Spu> records = result.getRecords();

        List<SpuDTO> spuList = SpuDTO.convertEntityList(records);
        for (SpuDTO spuDTO:spuList
             ) {
            handleCategoryAndBrandName(spuDTO);
        }

        return new PageDTO<>(total,pages,spuList);
    }

    @Transactional
    @Override
    public void saveGoods(SpuDTO spuDTO) {
        //1.新增Spu
        Spu spu = spuDTO.toEntity(Spu.class);
        spu.setSaleable(false);
        boolean success = save(spu);
        if (!success){
            throw new LgException(500,"新增商品失败!");
        }
        //2.新增SpuDetail
        Long spuId = spu.getId();
        SpuDetail spuDetail = spuDTO.getSpuDetail().toEntity(SpuDetail.class);
        spuDetail.setSpuId(spuId);
        success = spuDetailService.save(spuDetail);
        if (!success){
            throw new LgException(500,"新增商品详情失败");
        }
        //3.新增Sku
        List<Sku> skuList = new ArrayList<>();
        List<SkuDTO> skus = spuDTO.getSkus();
        for (SkuDTO sku:skus
             ) {
            Sku sku1 = sku.toEntity(Sku.class);
            sku1.setSpuId(spuId);
            sku1.setSaleable(false);
            skuList.add(sku1);
        }
        skuService.saveBatch(skuList);
    }
    @Transactional
    @Override
    public void updateSpuSaleable(Long id, Boolean saleable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
        boolean success = updateById(spu);
        if (!success) {
            throw new LgException(500, "更新失败");
        }
        success = skuService.update().eq("spu_id", id).set("saleable", saleable).update();
        if (!success) {
            throw new LgException(500, "更新失败");
        }

        //发送MQ消息
        String routingKey = saleable ? ITEM_UP_KEY : ITEM_DOWN_KEY;
        amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME, routingKey,id);
    }

    @Override
    public SpuDTO queryGoodsById(Long id) {
        //1.查询spu
        Spu spu = getById(id);
        if (spu == null){
            throw new LgException(400,"商品id不存在!");
        }
        //2.转换成DTO
        SpuDTO spuDTO = new SpuDTO(spu);

        //3.查询spuDetail
        SpuDetail detail = spuDetailService.getById(id);
        if(detail == null){
            throw new LgException(400,"商品id不存在");
        }
        spuDTO.setSpuDetail(new SpuDetailDTO(detail));

        //4.查询sku
        List<Sku> skuList = skuService.query().eq("spu_id", id).list();
        if (CollectionUtils.isEmpty(skuList)){
            throw new LgException(400,"商品id不存在");
        }
        spuDTO.setSkus(SkuDTO.convertEntityList(skuList));
        handleCategoryAndBrandName(spuDTO);
        return spuDTO;
    }

    @Transactional
    @Override
    public void updateGoods(SpuDTO spuDTO) {
        // 1.修改spu
        // 1.1.判断是否存在spu的id，有说明需要修改，没有说明不需要
        Long spuDTOId = spuDTO.getId();
        if (spuDTO != null){
            // 1.2.spu需要修改，更新spu, 转换dto
            Spu spu = spuDTO.toEntity(Spu.class);
            boolean success = updateById(spu);
            if (!success){
                throw new LgException(500,"更新商品失败！");
            }
        }
        // 2.修改spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        // 2.1.判断是否为null
        if (spuDetail != null && spuDetail.getSpuId() != null){
            // 2.2.spuDetail存在，需要修改，转换DTO
            SpuDetail detail = spuDetail.toEntity(SpuDetail.class);
            boolean success = spuDetailService.updateById(detail);
            if (!success){
                throw new LgException(500,"商品更新失败！");
            }
        }
        // 3.修改sku
        List<SkuDTO> skus = spuDTO.getSkus();
        // 3.1.判断是否包含sku
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        // 3.2.转换DTO，并将sku根据saleable是否为null来分组。null，是新增或修改，不是null是删除
        Map<Boolean, List<Sku>> collect = skus.stream().map(skuDTO -> skuDTO.toEntity(Sku.class))
                .collect(Collectors.groupingBy(sku -> sku.getSaleable() == null));
        // 3.3.获取要新增或修改的sku
        List<Sku> skuList = collect.get(false);
        if (!CollectionUtils.isEmpty(skuList)){
            List<Long> idList = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            skuService.removeByIds(idList);
        }
        List<Sku> skusList1 = collect.get(true);
        if (!CollectionUtils.isEmpty(skuList)){
            skuService.saveOrUpdateBatch(skuList);
        }

    }

    private void handleCategoryAndBrandName(SpuDTO spuDTO) {
        Brand brand = brandService.getById(spuDTO.getBrandId());
        if(brand != null) {
            spuDTO.setBrandName(brand.getName());
        }
        // 根据三级分类id查询分类集合
        List<Category> categories = categoryService.listByIds(spuDTO.getCategoryIds());
        if(!CollectionUtils.isEmpty(categories)) {
            // 取出分类的名称，拼接起来
            String names = categories.stream().map(Category::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(names);
        }
    }
}
