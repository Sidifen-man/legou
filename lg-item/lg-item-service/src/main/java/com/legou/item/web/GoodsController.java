package com.legou.item.web;


import com.legou.common.dto.PageDTO;
import com.legou.item.dto.SkuDTO;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.dto.SpuDTO;
import com.legou.item.dto.SpuDetailDTO;
import com.legou.item.entity.Sku;
import com.legou.item.service.SkuService;
import com.legou.item.service.SpuDetailService;
import com.legou.item.service.SpuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("goods")
public class GoodsController {
    private final SkuService skuService;
    private final SpuService spuService;
    private final SpuDetailService spuDetailService;


    public GoodsController(SpuService spuService, SpuDetailService spuDetailService, SkuService skuService) {
        this.skuService = skuService;
        this.spuService = spuService;
        this.spuDetailService = spuDetailService;
    }

    @GetMapping("/spu/page")
    public ResponseEntity<PageDTO<SpuDTO>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "id", required = false) Long id
    ){
        return ResponseEntity.ok(spuService.querySpuByPage(page,rows,saleable,categoryId,brandId,id));
    }
    @PostMapping
    public ResponseEntity<Void> saveGoodS(@RequestBody SpuDTO spuDTO){
        spuService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/saleable")
    public ResponseEntity<Void> updateSpuSaleable(
            @RequestParam("id") Long id,
            @RequestParam("saleable") Boolean saleable
    ){
    spuService.updateSpuSaleable(id,saleable);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpuDTO> queryGoodsById(@PathVariable("id") Long id){
        return ResponseEntity.ok(spuService.queryGoodsById(id));
    }
    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id") Long id){
        return ResponseEntity.ok(new SpuDetailDTO(spuDetailService.getById(id)));
    }
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("id") Long id){
        List<Sku> list = skuService.query().eq("spu_id", id).list();
        return ResponseEntity.ok(SkuDTO.convertEntityList(list));
    }
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(SkuDTO.convertEntityList(skuService.listByIds(ids)));
    }
    @GetMapping("/spec/value")
    public ResponseEntity<List<SpecParamDTO>> querySpecsValues(
            @RequestParam("id") Long id,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(spuDetailService.querySpecValues(id,searching));
    }
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(new SpuDTO(spuService.getById(id)));
    }
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> deductStock(@RequestBody Map<Long, Integer> cartMap){
        skuService.deductStock(cartMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/stock/plus")
    public ResponseEntity<Void> addStock(@RequestBody Map<Long, Integer> cartMap){
        skuService.addStock(cartMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
