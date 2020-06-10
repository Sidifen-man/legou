package com.legou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.common.exception.LgException;
import com.legou.item.dto.SpecParamDTO;
import com.legou.item.entity.SpecParam;
import com.legou.item.mapper.SpecParamMapper;
import com.legou.item.service.SpecParamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecParamServiceImpl extends ServiceImpl<SpecParamMapper, SpecParam> implements SpecParamService {
    @Override
    public List<SpecParamDTO> queryParams(Long categoryId, Long groupId, Boolean searching) {
        if (categoryId == null && groupId == null){
            throw new LgException(400,"查询条件不能为空！");
        }
        List<SpecParam> list = query().eq(categoryId != null, "category_id", categoryId)
                .eq(groupId != null, "group_id", groupId)
                .eq(searching != null, "searching", searching)
                .list();
        return SpecParamDTO.convertEntityList(list);

    }
}
