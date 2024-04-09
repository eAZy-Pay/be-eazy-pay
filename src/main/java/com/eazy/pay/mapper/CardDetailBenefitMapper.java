package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDetailBenefitDTO;
import com.eazy.pay.model.CardBenefit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardDetailBenefitMapper {
    CardDetailBenefitMapper INSTANCE = Mappers.getMapper(CardDetailBenefitMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "categoryId", source = "category.uid")
    @Mapping(target = "categoryName", source = "category.name")
    CardDetailBenefitDTO toDTO(CardBenefit cardBenefit);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "category.uid", source = "categoryId")
    @Mapping(target = "category.name", source = "categoryName")
    CardBenefit toEntity(CardDetailBenefitDTO cardDetailBenefitDTO);
}
