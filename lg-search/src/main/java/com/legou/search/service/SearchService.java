package com.legou.search.service;

import com.legou.search.dao.SearchParamDTO;
import com.legou.search.entity.Goods;
import com.legou.starter.elastic.entity.PageInfo;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SearchService {
    /**
     * 创建索引库并设置映射
     */
    void createIndexAndMapping();
    /**
     * 加载数据到索引库
     */
    void loadData();

    Mono<List<String>> getSuggestion(String key);

    Mono<PageInfo<Goods>> searchGoods(SearchParamDTO request);

    Mono<Map<String, List<?>>> getFilters(SearchParamDTO param);

    void saveGoodsById(Long spuId);

    void deleteGoodsById(Long spuId);
}
