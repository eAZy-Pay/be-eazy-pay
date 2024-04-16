package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDetailBenefitDTO;
import com.eazy.pay.dto.CardDetailBenefitDTO.CardDetailBenefitDTOBuilder;
import com.eazy.pay.model.CardBenefit;
import com.eazy.pay.model.Category;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-16T09:39:17+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.9 (JetBrains s.r.o.)"
)
public class CardDetailBenefitMapperImpl implements CardDetailBenefitMapper {

    @Override
    public CardDetailBenefitDTO toDTO(CardBenefit cardBenefit) {
        if ( cardBenefit == null ) {
            return null;
        }

        CardDetailBenefitDTOBuilder cardDetailBenefitDTO = CardDetailBenefitDTO.builder();

        cardDetailBenefitDTO.uid( cardBenefit.getUid() );
        cardDetailBenefitDTO.categoryId( cardBenefitCategoryUid( cardBenefit ) );
        cardDetailBenefitDTO.categoryName( cardBenefitCategoryName( cardBenefit ) );
        if ( cardBenefit.getBenefitRate() != null ) {
            cardDetailBenefitDTO.benefitRate( cardBenefit.getBenefitRate() );
        }

        return cardDetailBenefitDTO.build();
    }

    private Long cardBenefitCategoryUid(CardBenefit cardBenefit) {
        if ( cardBenefit == null ) {
            return null;
        }
        Category category = cardBenefit.getCategory();
        if ( category == null ) {
            return null;
        }
        Long uid = category.getUid();
        if ( uid == null ) {
            return null;
        }
        return uid;
    }

    private String cardBenefitCategoryName(CardBenefit cardBenefit) {
        if ( cardBenefit == null ) {
            return null;
        }
        Category category = cardBenefit.getCategory();
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
