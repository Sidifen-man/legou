package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.item.dto.SpecGroupDTO;
import com.legou.item.entity.SpecGroup;

import java.util.List;

public interface SpecGroupService extends IService<SpecGroup> {
    List<SpecGroupDTO> querySpecList(Long categoryId);
}
