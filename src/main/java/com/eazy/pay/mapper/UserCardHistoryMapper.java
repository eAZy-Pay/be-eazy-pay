package com.eazy.pay.mapper;

import com.eazy.pay.dto.UserCardHistoryDTO;
import com.eazy.pay.model.UserCardHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCardHistoryMapper {

    UserCardHistoryMapper INSTANCE = Mappers.getMapper(UserCardHistoryMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "userCardId", source = "userCard.uid")
    @Mapping(target = "yearAndMonth", source = "yearAndMonth")
    @Mapping(target = "benefitAmount", source = "benefitAmount")
    @Mapping(target = "useAmount", source = "useAmount")
    @Mapping(target = "isFulfilled", source = "isFulfilled")
    UserCardHistoryDTO toDTO(UserCardHistory userCardHistory);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "userCard.uid", source = "userCardId")
    @Mapping(target = "yearAndMonth", source = "yearAndMonth")
    @Mapping(target = "benefitAmount", source = "benefitAmount")
    @Mapping(target = "useAmount", source = "useAmount")
    @Mapping(target = "isFulfilled", source = "isFulfilled")
    UserCardHistory toEntity(UserCardHistoryDTO userCardHistoryDTO);
}
