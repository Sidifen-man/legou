package com.legou.search.web;

import com.legou.search.dao.SearchParamDTO;
import com.legou.search.entity.Goods;
import com.legou.search.service.SearchService;
import com.legou.starter.elastic.entity.PageInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequestMapping("goods")
@RestController
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/initialization")
    public ResponseEntity<String> init(){
        searchService.createIndexAndMapping();
        searchService.loadData();
        return ResponseEntity.ok("导入成功");
    }
    @GetMapping("/suggestion")
    public Mono<List<String>> getSuggestion(@RequestParam("key") String key){
        return searchService.getSuggestion(key);
    }
    /**
     * 分页搜索商品数据
     * @param request 请求参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Mono<PageInfo<Goods>> searchGoods(@RequestBody SearchParamDTO request){
        return searchService.searchGoods(request);
    }

    @PostMapping("/filter")
    public Mono<Map<String,List<?>>> getFilters(@RequestBody SearchParamDTO param){
        return searchService.getFilters(param);
    }

}
