package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.SpecParam;

import java.util.List;

public interface SpecParamService extends IService<SpecParam> {
    List<SpecParamDTO> queryParams(Long categoryId, Long groupId, Boolean searching);

}
