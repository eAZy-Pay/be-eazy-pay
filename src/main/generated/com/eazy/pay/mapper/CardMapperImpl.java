package com.eazy.pay.mapper;

import com.eazy.pay.dto.CardDTO;
import com.eazy.pay.dto.CardDTO.CardDTOBuilder;
import com.eazy.pay.model.Card;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-16T09:39:17+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.9 (JetBrains s.r.o.)"
)
public class CardMapperImpl implements CardMapper {

    @Override
    public CardDTO toDTO(Card card) {
        if ( card == null ) {
            return null;
        }

        CardDTOBuilder cardDTO = CardDTO.builder();

        cardDTO.uid( card.getUid() );
        cardDTO.image( card.getImage() );
        cardDTO.name( card.getName() );
        cardDTO.annualFee( card.getAnnualFee() );
        cardDTO.performance( card.getPerformance() );
        cardDTO.benefitLimit( card.getBenefitLimit() );
        cardDTO.info( card.getInfo() );
        cardDTO.applicationUrl( card.getApplicationUrl() );

        return cardDTO.build();
    }
}
