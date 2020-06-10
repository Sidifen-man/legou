package com.legou.common.dto;

import com.legou.common.entity.BaseEntity;
import com.legou.common.utils.BeanHelper;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public abstract class BaseDTO {

    public <T> T toEntity(Class<T> entityClass) {
        return BeanHelper.copyProperties(this, entityClass);
    }

    public BaseDTO(BaseEntity entity) {
        if(entity != null){
            BeanUtils.copyProperties(entity, this);
        }
    }

    public BaseDTO() {
    }
}
