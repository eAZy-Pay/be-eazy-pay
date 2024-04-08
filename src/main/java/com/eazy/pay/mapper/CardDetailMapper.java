package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDetailDTO;
import com.eazy.pay.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardDetailMapper {
    CardDetailMapper INSTANCE = Mappers.getMapper(CardDetailMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "cardBenefitList", source = "cardBenefitList")
    CardDetailDTO toDTO(Card card);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "cardBenefitList", source = "cardBenefitList")
    Card toEntity(CardDetailDTO cardDetailDTO);

}
