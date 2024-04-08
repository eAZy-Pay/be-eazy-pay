package com.eazy.pay.mapper;

import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.model.MonthlyFor6;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MonthlyFor6ResponseMapper {
    MonthlyFor6ResponseMapper INSTANCE = Mappers.getMapper(MonthlyFor6ResponseMapper.class);

    @Mapping(target = "categoryId", source = "category.uid")
    @Mapping(target = "categoryName", source = "category.name")
    MonthlyFor6ResponseDTO toDTO(MonthlyFor6 monthlyFor6);

}
