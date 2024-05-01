package com.eazy.pay.mapper;

import com.eazy.pay.dto.UserCategoryHistoryDTO;
import com.eazy.pay.model.UserCategoryHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCategoryHistoryMapper {
    UserCategoryHistoryMapper INSTANCE = Mappers.getMapper(UserCategoryHistoryMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "categoryId", source = "category.uid")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "benefitAmount", source = "benefitAmount")
    @Mapping(target = "useAmount", source = "useAmount")
    UserCategoryHistoryDTO toDTO(UserCategoryHistory userCategoryHistory);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "category.uid", source = "categoryId")
    @Mapping(target = "benefitAmount", source = "benefitAmount")
    @Mapping(target = "useAmount", source = "useAmount")
    UserCategoryHistory toEntity(UserCategoryHistoryDTO userCategoryHistoryDTO);

}
