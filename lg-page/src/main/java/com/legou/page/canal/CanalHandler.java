package com.legou.page.canal;

import com.legou.page.service.GoodsPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.context.CanalContext;
import top.javatool.canal.client.handler.EntryHandler;

import java.util.Map;

@Component
@Slf4j
@CanalTable(value = "all")
public class CanalHandler implements EntryHandler<Map<String,String>> {
    @Autowired
    private GoodsPageService goodsPageService;
    //新增
    @Override
    public void insert(Map<String,String> model){
        String table = CanalContext.getModel().getTable();
        if ("tb_sky".equals(table)){
            log.info("sku新增了{}",model);
            goodsPageService.loadSkuListData(Long.valueOf(model.get("spu_id")));
        }
    }

    //修改
    @Override
    public void update(Map<String,String> before,Map<String,String> after){
        String table = CanalContext.getModel().getTable();
        if ("tb_sku".equals(table)){
            log.info("sku修改了{}",after);
            goodsPageService.loadSkuListData(Long.valueOf(after.get("spu_id")));
        }
    }
    //内容已删除
    @Override
    public void delete(Map<String,String> model){
        String table = CanalContext.getModel().getTable();
        if("tb_sku".equals(table)){
            log.info("sku删除了{}",model);
            goodsPageService.deleteSku(Long.valueOf(model.get("spu_id")));
        }
    }
}
