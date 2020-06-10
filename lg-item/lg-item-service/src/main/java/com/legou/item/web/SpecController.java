package com.legou.item.web;

import com.legou.item.dto.SpecGroupDTO;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.SpecGroup;
import com.legou.item.entity.SpecParam;
import com.legou.item.service.SpecGroupService;
import com.legou.item.service.SpecParamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {
    private SpecGroupService specGroupService;
    private SpecParamService specParamService;
    public SpecController(SpecGroupService specGroupService,SpecParamService specParamService){
        this.specGroupService = specGroupService;
        this.specParamService = specParamService;
    }

    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> queryGroupByCategory(@RequestParam("id") Long id){
        return ResponseEntity.ok(SpecGroupDTO.convertEntityList(specGroupService
                .query().eq("category_id",id).list()));
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParams(
            @RequestParam(value = "categoryId",required = false) Long categoryId,
            @RequestParam(value = "groupId", required = false) Long groupId,
            @RequestParam(value = "searching", required = false) Boolean searching
    ){
        return ResponseEntity.ok(specParamService.queryParams(categoryId, groupId, searching));
    }
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroup(@RequestBody SpecGroupDTO groupDTO){
        specGroupService.save(groupDTO.toEntity(SpecGroup.class));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroupDTO groupDTO){
        specGroupService.updateById(groupDTO.toEntity(SpecGroup.class));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/param")
    public ResponseEntity<Void> saveParam(@RequestBody SpecParamDTO paramDTO){
        specParamService.save(paramDTO.toEntity(SpecParam.class));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/param")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParamDTO paramDTO){
        specParamService.updateById(paramDTO.toEntity(SpecParam.class));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/list")
    public ResponseEntity<List<SpecGroupDTO>> querySpecList(@RequestParam("id") Long categoryId){
        return ResponseEntity.ok(specGroupService.querySpecList(categoryId));
    }
}
