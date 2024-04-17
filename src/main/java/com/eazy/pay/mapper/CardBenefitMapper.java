package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardBenefitDTO;
import com.eazy.pay.model.CardBenefit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardBenefitMapper {
    CardBenefitMapper INSTANCE = Mappers.getMapper(CardBenefitMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "categoryId", source = "category.uid")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "benefitRate", source = "benefitRate")
    CardBenefitDTO toDTO(CardBenefit cardBenefit);

}
