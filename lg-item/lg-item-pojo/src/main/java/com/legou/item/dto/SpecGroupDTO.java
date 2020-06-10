package com.legou.item.dto;

import com.legou.common.dto.BaseDTO;
import com.legou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SpecGroupDTO extends BaseDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private List<SpecParamDTO> params;
    public SpecGroupDTO(BaseEntity entity){
        super(entity);
    }
    public static <T extends BaseEntity> List<SpecGroupDTO> convertEntityList(Collection<T> list) {
        return list.stream().map(SpecGroupDTO::new).collect(Collectors.toList());
    }
}
