package com.legou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.SpuDetail;

import java.util.List;

public interface SpuDetailService extends IService<SpuDetail> {
    List<SpecParamDTO> querySpecValues(Long id, Boolean searching);
}
