package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

// CardBenefitMapper 사용한다
@Mapper(uses = {CardBenefitMapper.class, CardMapper.class})
public interface CardWithBenefitMapper {
    CardWithBenefitMapper INSTANCE = Mappers.getMapper(CardWithBenefitMapper.class);

    @Mapping(target = "card", expression = "java(CardMapper.INSTANCE.toDTO(card))")
    @Mapping(target = "benefitList", source = "benefitList")
    CardWithBenefitDTO toDTO(Card card);
}