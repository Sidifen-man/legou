package com.legou.item.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.legou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_spec_group")
@EqualsAndHashCode(callSuper = false)
public class SpecGroup extends BaseEntity {
    @TableId
    private Long id;
    private Long categoryId;
    private String name;
}
