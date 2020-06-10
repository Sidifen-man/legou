package com.legou.item.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.legou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_spu_detail")
@EqualsAndHashCode(callSuper = false)
public class SpuDetail extends BaseEntity {
    @TableId(type = IdType.INPUT)
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    private String specification;// 规格参数值
}
