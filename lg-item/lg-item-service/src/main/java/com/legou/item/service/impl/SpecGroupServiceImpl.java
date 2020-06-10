package com.legou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.item.dto.SpecGroupDTO;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.SpecGroup;
import com.legou.item.mapper.SpecGroupMapper;
import com.legou.item.service.SpecGroupService;
import com.legou.item.service.SpecParamService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecGroupServiceImpl extends ServiceImpl<SpecGroupMapper, SpecGroup> implements SpecGroupService {
    private final SpecParamService paramService;

    public SpecGroupServiceImpl(SpecParamService paramService) {
        this.paramService = paramService;
    }

    @Override
    public List<SpecGroupDTO> querySpecList(Long categoryId) {
        //查询规格组
        List<SpecGroupDTO> groupList = SpecGroupDTO.convertEntityList(query().eq("category_id", categoryId).list());
        if (CollectionUtils.isEmpty(groupList)){
            throw new LgException(404,"该分类下的规格组不存在！");
        }
        //查询规格参数
        List<SpecParamDTO> paramDTOList = paramService.queryParams(categoryId, null, null);
        Map<Long,List<SpecParamDTO>> map =  paramDTOList.stream().collect(Collectors.groupingBy(SpecParamDTO::getCategoryId));
        for (SpecGroupDTO groupDTO : groupList) {
            groupDTO.setParams(map.get(groupDTO.getId()));
        }
        return groupList;
    }
}
