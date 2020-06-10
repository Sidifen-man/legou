package com.legou.item.web;

import com.legou.item.dto.CategoryDTO;
import com.legou.item.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping("{id}")
    public ResponseEntity<CategoryDTO> queryCategoryById(@PathVariable("id") Long id){
        return ResponseEntity.ok(new CategoryDTO(categoryService.getById(id)));
    }

    @GetMapping("list")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByList(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(CategoryDTO.convertEntityList(categoryService.listByIds(ids)));
    }

    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByParentId(@RequestParam("pid") Long pid){
        return ResponseEntity.ok(CategoryDTO.convertEntityList(categoryService.query().eq("parent_id",pid).list()));
    }

    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByBrandId(@RequestParam("id") Long brandId){
        return ResponseEntity.ok(categoryService.queryCategoryByBrandId(brandId));
    }

}
