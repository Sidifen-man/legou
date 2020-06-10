package com.legou.item.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.legou.common.dto.PageDTO;
import com.legou.item.dto.BrandDTO;
import com.legou.item.entity.CategoryBrand;
import com.legou.item.service.BrandService;
import com.legou.item.service.CategoryBrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    private final BrandService brandService;
    private final CategoryBrandService categoryBrandService;
    public BrandController(BrandService brandService, CategoryBrandService categoryBrandService){
        this.brandService = brandService;
        this.categoryBrandService = categoryBrandService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> queryBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(new BrandDTO(brandService.getById(id)));
    }

    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> queryBrandByIds(@RequestParam("ids") List<Long> idList){
        return ResponseEntity.ok(BrandDTO.convertEntityList(brandService.listByIds(idList)));
    }

    @GetMapping("/page")
    public ResponseEntity<PageDTO<BrandDTO>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "key", required = false)String key
    ){
        return ResponseEntity.ok(brandService.queryBrandByPage(page,rows,key));
    }
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCategory(@RequestParam("id") Long id){
        return ResponseEntity.ok(brandService.queryBrandByCategory(id));
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(BrandDTO brandDTO){
        brandService.saveBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandDTO brandDTO){
        brandService.updateBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable("id") Long id){
        brandService.removeById(id);
        categoryBrandService.remove(
                new QueryWrapper<CategoryBrand>().eq("brand_id", id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
