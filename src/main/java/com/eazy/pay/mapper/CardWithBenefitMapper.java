package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// CardBenefitMapper 사용한다
@Mapper(uses = {CardBenefitMapper.class})
public interface CardWithBenefitMapper {
    CardWithBenefitMapper INSTANCE = Mappers.getMapper(CardWithBenefitMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "annualFee", source = "annualFee")
    @Mapping(target = "performance", source = "performance")
    @Mapping(target = "benefitLimit", source = "benefitLimit")
    @Mapping(target = "info", source = "info")
    @Mapping(target = "applicationUrl", source = "applicationUrl")
    @Mapping(target = "benefitList", source = "benefitList")
    CardWithBenefitDTO toDTO(Card card);
}