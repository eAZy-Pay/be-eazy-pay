package com.eazy.pay.mapper;

import com.eazy.pay.dto.CategoryDTO;
import com.eazy.pay.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDTO(Category category);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "name", source = "name")
    Category toEntity(CategoryDTO categoryDTO);

}
