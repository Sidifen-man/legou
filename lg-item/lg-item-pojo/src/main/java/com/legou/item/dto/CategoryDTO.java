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
@EqualsAndHashCode(callSuper = true)
public class CategoryDTO extends BaseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;

    public CategoryDTO(BaseEntity entity) {
        super(entity);
    }
    public static <T extends BaseEntity> List<CategoryDTO> convertEntityList(Collection<T> list){
        if(list == null){
            return null;
        }
        return list.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }
}

