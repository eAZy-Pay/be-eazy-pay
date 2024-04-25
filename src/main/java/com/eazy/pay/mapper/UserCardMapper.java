package com.eazy.pay.mapper;

import com.eazy.pay.dto.UserCardDTO;
import com.eazy.pay.model.UserCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCardMapper {
    UserCardMapper INSTANCE = Mappers.getMapper(UserCardMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "userId", source = "user.uid")
    @Mapping(target = "cardId", source = "card.uid")
    @Mapping(target = "num", source = "num")
    @Mapping(target = "expirationDate", source = "expirationDate")
    @Mapping(target = "paymentLimit", source = "paymentLimit")
    UserCardDTO toDTO(UserCard entity);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "user.uid", source = "userId")
    @Mapping(target = "card.uid", source = "cardId")
    @Mapping(target = "num", source = "num")
    @Mapping(target = "expirationDate", source = "expirationDate")
    @Mapping(target = "paymentLimit", source = "paymentLimit")
    UserCard toEntity(UserCardDTO dto);
}
