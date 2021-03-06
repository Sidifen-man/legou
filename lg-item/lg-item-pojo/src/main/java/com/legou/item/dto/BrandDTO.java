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
public class BrandDTO extends BaseDTO {
    private Long id;
    private String name;
    private String image;
    private Character letter;
    private List<Long> categoryIds;

    public BrandDTO(BaseEntity entity) {
        super(entity);
    }
    public  static <T extends BaseEntity> List<BrandDTO> convertEntityList(Collection<T> list){
        if (list == null){
            return null;
        }
        return list.stream().map(BrandDTO::new).collect(Collectors.toList());
    }
}
