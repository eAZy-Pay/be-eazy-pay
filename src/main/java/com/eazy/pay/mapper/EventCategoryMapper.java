package com.eazy.pay.mapper;

import com.eazy.pay.dto.EventCategoryDTO;
import com.eazy.pay.model.EventCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventCategoryMapper {

    EventCategoryMapper INSTANCE = Mappers.getMapper(EventCategoryMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "name", source = "name")
    EventCategoryDTO toDTO(EventCategory eventCategory);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "name", source = "name")
    EventCategory toEntity(EventCategoryDTO eventCategoryDTO);

}
