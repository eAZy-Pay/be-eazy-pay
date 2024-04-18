package com.eazy.pay.dto;

import com.eazy.pay.model.Card;
import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BenefitAndSimpleUserCardsDTO {
    private int benefitAmount;
    private List<SimpleUserCardDTO> cards;
}
