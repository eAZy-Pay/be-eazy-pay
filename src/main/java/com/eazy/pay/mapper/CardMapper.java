package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Mapping(target = "uid", source = "uid")
    CardDTO toDTO(Card card);


    @Mapping(target = "uid", source = "uid")
    Card toEntity(CardDTO cardDTO);
}
