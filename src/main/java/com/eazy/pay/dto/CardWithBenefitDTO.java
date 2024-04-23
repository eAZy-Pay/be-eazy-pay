package com.eazy.pay.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardWithBenefitDTO {
    private CardDTO card;
    private List<CardBenefitDTO> benefitList;
}
