package com.legou.item.dto;

import com.legou.common.dto.BaseDTO;
import com.legou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpuDetailDTO extends BaseDTO {
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    private String specification;// 规格参数
    public SpuDetailDTO(BaseEntity entity){
        super(entity);
    }
}
