package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDetailBenefitDTO;
import com.eazy.pay.dto.CardDetailDTO;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = {CardDetailBenefitMapper.class})
public interface CardDetailMapper {
    CardDetailMapper INSTANCE = Mappers.getMapper(CardDetailMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "benefitList", source = "benefitList")
    CardDetailDTO toDTO(Card card);

}
