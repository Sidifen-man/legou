package com.legou.search.service.impl;

import com.legou.common.dto.PageDTO;
import com.legou.common.exception.LgException;
import com.legou.item.client.ItemClient;
import com.legou.item.dto.*;
import com.legou.search.constants.SearchConstants;
import com.legou.search.dao.SearchParamDTO;
import com.legou.search.entity.Goods;
import com.legou.search.repository.GoodsRepository;
import com.legou.search.service.SearchService;
import com.legou.starter.elastic.entity.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static com.legou.search.constants.SearchConstants.*;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService  {

    private final GoodsRepository goodsRepository;
    private final ItemClient itemClient;

    public SearchServiceImpl(GoodsRepository goodsRepository, ItemClient itemClient) {
        this.goodsRepository = goodsRepository;
        this.itemClient = itemClient;
    }

    @Override
    public void createIndexAndMapping() {
        try {
            goodsRepository.deleteIndex();
        }catch (Exception e){
            log.error("删除失败...",e);
        }
        goodsRepository.createIndex("{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"my_pinyin\": {\n" +
                "          \"tokenizer\": \"ik_smart\",\n" +
                "          \"filter\": [\n" +
                "            \"py\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"py\": {\n" +
                "\t\t  \"type\": \"pinyin\",\n" +
                "          \"keep_full_pinyin\": true,\n" +
                "          \"keep_joined_full_pinyin\": true,\n" +
                "          \"keep_original\": true,\n" +
                "          \"limit_first_letter_length\": 16,\n" +
                "          \"remove_duplicated_term\": true\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"suggestion\": {\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"image\":{\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"updateTime\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"specs\":{\n" +
                "        \"type\": \"nested\",\n" +
                "        \"properties\": {\n" +
                "          \"name\":{\"type\": \"keyword\" },\n" +
                "          \"value\":{\"type\": \"keyword\" }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

    @Override
    public void loadData() {
        //分页查询初始化
        int page = 1,rows = 100;
        while (true){
            //查询商品spu
            PageDTO<SpuDTO> result = itemClient.querySpuByPage(page, rows, true, null, null, null);
            List<SpuDTO> items = result.getItems();
            //将spu封装到goods中
            List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO item:items
            ) {
                Goods goods = buildGoods(item);
                goodsList.add(goods);
            }
            goodsRepository.saveAll(goodsList);
            page++;
            Long totalPage = result.getTotalPage();
            if (page>totalPage){
                break;
            }
        }
    }

    @Override
    public Mono<List<String>> getSuggestion(String key) {
        Map<String,Object> params = new HashMap<>();
        params.put(SearchConstants.SUGGESTION_PARAM_PREFIX_KEY,key);
        params.put(SearchConstants.SUGGESTION_PARAM_FIELD,SUGGESTION_FIELD);
        return goodsRepository.suggestByTemplate(SearchConstants.SUGGESTION_TEMPLATE_ID,params);
    }

    @Override
    public Mono<PageInfo<Goods>> searchGoods(SearchParamDTO request) {
        //创建查询条件工厂的对象作为载体
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //筛选出一些想要的字段
        builder.fetchSource(DEFAULT_SOURCE_FIELD,new String[0]);
        builder.query(buildBasicQuery(request));
        //排序条件
        String sortBy = request.getSortBy();
        if (StringUtils.isNotBlank(sortBy)){
            builder.sort(sortBy,request.getDesc()? SortOrder.DESC:SortOrder.ASC);
        }
        //分页条件
        builder.from(request.getFrom()).size(request.getSize());
        //高亮条件
        builder.highlighter(new HighlightBuilder().field(DEFAULT_SEARCH_FIELD)
                .preTags(DEFAULT_PRE_TAG).preTags(DEFAULT_POST_TAG));
        //查询并返回结果
        return goodsRepository.queryBySourceBuilderForPageHighlight(builder);
    }

    private QueryBuilder buildBasicQuery(SearchParamDTO request) {
        //查询条件
        //健壮性考虑，查询条件不能为空
        if (StringUtils.isBlank(request.getKey())) {
            throw new LgException(400, "请求参数不能为空!");
        }
        /**
          *@Author Haoran·Zhao
          *@Date 2020/5/28
          *@Version 1.0
          *filters为什么报错？
          */
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //关键字查询,must
        queryBuilder.must(QueryBuilders.matchQuery(DEFAULT_SEARCH_FIELD, request.getKey()));
        //过滤项查询，filter
        Map<String, String> filters = request.getFilters();

        //判断是否有过滤项
//        filters.isEmpty()
        if (CollectionUtils.isEmpty(filters)){
            return queryBuilder;
        }
        // 添加过滤项, {分类, 品牌, CPU频率}
        for (Map.Entry<String,String> entry:filters.entrySet()
             ) {
            //获取key
            String key = entry.getKey();
            //获取值
            String value = entry.getValue();
            if ("分类".equals(key)){
                queryBuilder.filter(QueryBuilders.termQuery(CATEGORY_FIELD_NAME,value));
            }else if ("品牌".equals(key)){
                queryBuilder.filter(QueryBuilders.termQuery(BRAND_FIELD_NAME,value));
            }else{
                //按照规格参数处理，nested类型
                //创建布尔查询，组合name和value
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.must(QueryBuilders.termQuery(SPEC_NAME_FIELD_NAME,key));
                query.must(QueryBuilders.termQuery(SPEC_VALUE_FIELD_NAME,value));
                //添加到nested查询中
                queryBuilder.filter(QueryBuilders.nestedQuery(SPEC_FIELD_PATH,query, ScoreMode.None));
            }
        }
        //绑定
       return queryBuilder ;
    }

    @Override
    public Mono<Map<String, List<?>>> getFilters(SearchParamDTO param) {
        //构建查询条件的工厂对象
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //查询条件
        builder.query(buildBasicQuery(param));
        ///分页设置为0，不需要商品数据，只要聚合结果
        builder.size(0);
        //添加聚合条件
        //分类聚合
        builder.aggregation(AggregationBuilders.terms("categoryAgg").field(CATEGORY_FIELD_NAME));
        //品牌的聚合
        builder.aggregation(AggregationBuilders.terms("brandAgg").field(BRAND_FIELD_NAME));
        //规格参数nested聚合
        builder.aggregation(AggregationBuilders.nested("specAgg",SPEC_FIELD_PATH)
                .subAggregation(AggregationBuilders.terms("nameAgg").field(SPEC_NAME_FIELD_NAME)
                        .subAggregation(AggregationBuilders.terms("valueAgg").field(SPEC_VALUE_FIELD_NAME))));
        //请求聚合查询
        Mono<Aggregations> aggregationsMono = goodsRepository.aggregationBySourceBuilder(builder);
        //分析结果

        return aggregationsMono.map(aggregations -> {
            //定义map存放结果
            Map<String, List<?>> map = new HashMap<>();
            //解析分类结果，根据名称获得聚合结果
            Terms categoryAgg = aggregations.get("categoryAgg");
            //解析聚合结果，得到分类id
            List<Long> categoryIdList = getIdsFromBucket(categoryAgg);
            List<CategoryDTO> categories = itemClient.queryCategoryByList(categoryIdList);
            map.put("分类",categories);

            //解析分类结果，根据名称获得聚合结果
            Terms brandAgg = aggregations.get("brandAgg");
            //解析聚合结果，得到分类id
            List<Long> brandIdList = getIdsFromBucket(brandAgg);
            List<BrandDTO> brands = itemClient.queryBrandByIds(brandIdList);
            map.put("品牌",brands);

            //解析nested聚合
            Nested specAgg = aggregations.get("specAgg");
            // 获取对规格参数名称的聚合结果
            Terms nameAgg = specAgg.getAggregations().get("nameAgg");
            //获取buckets，并且遍历
            for (Terms.Bucket bucket:nameAgg.getBuckets()
                 ) {
                //从bucket中获取key，就是规格参数的名称
                String specName = bucket.getKeyAsString();
                //获取当前规格参数的值的聚合结果
                Terms valueAgg = bucket.getAggregations().get("valueAgg");
                List<String> values = valueAgg.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                map.put(specName,values);
            }

            return map;
        });
    }

    @Override
    public void saveGoodsById(Long spuId) {
        SpuDTO spu = itemClient.querySpuById(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //写入索引库
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoodsById(Long spuId) {
        goodsRepository.deleteById(spuId);
    }

    private List<Long> getIdsFromBucket(Terms categoryAgg) {
        return categoryAgg.getBuckets().stream().map(Terms.Bucket::getKeyAsNumber)
                .map(Number::longValue).collect(Collectors.toList());
    }

    private Goods buildGoods(SpuDTO item) {
        //自动补全提示字段
        List<String> suggestionList = new ArrayList<>(
                Arrays.asList(StringUtils.split(item.getCategoryName(), "/")));
        suggestionList.add(item.getName());
        suggestionList.add(item.getBrandName());

        //sku的价格集合
        List<SkuDTO> skus = item.getSkus();
        if (CollectionUtils.isEmpty(skus)){
             skus = itemClient.querySkuBySpuId(item.getId());
        }
        Set<Long> prices = skus.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());

        //商品销量
        long soldSum = skus.stream().mapToLong(SkuDTO::getSold).sum();

        //sku的某个图片
        String images = skus.get(0).getImages();
        String image = StringUtils.substringBefore(images, ",");

        //规格参数
        List<Map<String,Object>> specs = new ArrayList<>();
        List<SpecParamDTO> paramDTOList = itemClient.querySpecsValues(item.getId(), true);
        for (SpecParamDTO parm:paramDTOList
             ) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("name",parm.getName());
            map.put("value",chooseSegment(parm));
            specs.add(map);
        }

        Goods goods = new Goods();
        goods.setUpdateTime(new Date());
        goods.setTitle(item.getTitle());

        //自动补全提示字段
        goods.setSuggestion(suggestionList);
        //规格参数
        goods.setSpecs(specs);
        //商品销量
        goods.setSold(soldSum);
        //sku的价格集合
        goods.setPrices(prices);
        //sku的某个图片
        goods.setImage(image);
        goods.setId(item.getId());
        goods.setCategoryId(item.getCid3());
        goods.setBrandId(item.getBrandId());
        return goods;
    }

    private Object chooseSegment(SpecParamDTO p) {
        Object value = p.getValue();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        if(!p.getNumeric() || StringUtils.isBlank(p.getSegments()) || value instanceof Collection){
            return value;
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }
}
