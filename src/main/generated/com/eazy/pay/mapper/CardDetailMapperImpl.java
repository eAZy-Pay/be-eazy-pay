package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDetailBenefitDTO;
import com.eazy.pay.dto.CardDetailDTO;
import com.eazy.pay.dto.CardDetailDTO.CardDetailDTOBuilder;
import com.eazy.pay.model.Card;
import com.eazy.pay.model.CardBenefit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.mapstruct.factory.Mappers;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-16T09:39:17+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.9 (JetBrains s.r.o.)"
)
public class CardDetailMapperImpl implements CardDetailMapper {

    private final CardDetailBenefitMapper cardDetailBenefitMapper = Mappers.getMapper( CardDetailBenefitMapper.class );

    @Override
    public CardDetailDTO toDTO(Card card) {
        if ( card == null ) {
            return null;
        }

        CardDetailDTOBuilder cardDetailDTO = CardDetailDTO.builder();

        cardDetailDTO.uid( card.getUid() );
        cardDetailDTO.benefitList( cardBenefitListToCardDetailBenefitDTOList( card.getBenefitList() ) );
        cardDetailDTO.image( card.getImage() );
        cardDetailDTO.name( card.getName() );
        cardDetailDTO.annualFee( card.getAnnualFee() );
        cardDetailDTO.performance( card.getPerformance() );
        cardDetailDTO.benefitLimit( card.getBenefitLimit() );
        cardDetailDTO.info( card.getInfo() );
        cardDetailDTO.applicationUrl( card.getApplicationUrl() );

        return cardDetailDTO.build();
    }

    protected List<CardDetailBenefitDTO> cardBenefitListToCardDetailBenefitDTOList(List<CardBenefit> list) {
        if ( list == null ) {
            return null;
        }

        List<CardDetailBenefitDTO> list1 = new ArrayList<CardDetailBenefitDTO>( list.size() );
        for ( CardBenefit cardBenefit : list ) {
            list1.add( cardDetailBenefitMapper.toDTO( cardBenefit ) );
        }

        return list1;
    }
}
