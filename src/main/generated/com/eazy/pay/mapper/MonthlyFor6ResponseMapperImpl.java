package com.eazy.pay.mapper;

import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.dto.MonthlyFor6ResponseDTO.MonthlyFor6ResponseDTOBuilder;
import com.eazy.pay.model.Category;
import com.eazy.pay.model.MonthlyFor6;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-16T09:39:17+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.9 (JetBrains s.r.o.)"
)
public class MonthlyFor6ResponseMapperImpl implements MonthlyFor6ResponseMapper {

    @Override
    public MonthlyFor6ResponseDTO toDTO(MonthlyFor6 monthlyFor6) {
        if ( monthlyFor6 == null ) {
            return null;
        }

        MonthlyFor6ResponseDTOBuilder monthlyFor6ResponseDTO = MonthlyFor6ResponseDTO.builder();

        monthlyFor6ResponseDTO.categoryId( monthlyFor6CategoryUid( monthlyFor6 ) );
        monthlyFor6ResponseDTO.categoryName( monthlyFor6CategoryName( monthlyFor6 ) );
        monthlyFor6ResponseDTO.useAmount( monthlyFor6.getUseAmount() );

        return monthlyFor6ResponseDTO.build();
    }

    private Long monthlyFor6CategoryUid(MonthlyFor6 monthlyFor6) {
        if ( monthlyFor6 == null ) {
            return null;
        }
        Category category = monthlyFor6.getCategory();
        if ( category == null ) {
            return null;
        }
        Long uid = category.getUid();
        if ( uid == null ) {
            return null;
        }
        return uid;
    }

    private String monthlyFor6CategoryName(MonthlyFor6 monthlyFor6) {
        if ( monthlyFor6 == null ) {
            return null;
        }
        Category category = monthlyFor6.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
