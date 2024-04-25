package com.eazy.pay.mapper;

import com.eazy.pay.dto.HighlightedCardDTO;
import com.eazy.pay.model.HighlightedCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HighlightedCardMapper {
    HighlightedCardMapper INSTANCE = Mappers.getMapper(HighlightedCardMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "eventCategoryId", source = "eventCategory.uid")
    @Mapping(target = "eventCategoryName", source = "eventCategory.name")
    @Mapping(target = "displayOrder", source = "displayOrder")
    HighlightedCardDTO toDTO(HighlightedCard card);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "eventCategory.uid", source = "eventCategoryId")
    @Mapping(target = "eventCategory.name", source = "eventCategoryName")
    @Mapping(target = "displayOrder", source = "displayOrder")
    HighlightedCard toEntity(HighlightedCardDTO cardDTO);
}
