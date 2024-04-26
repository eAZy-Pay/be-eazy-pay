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

    private int totalBenefitAmount;
    private int totalPaymentLimit;
    private int totalUsedAmount;
    private int availableFunds;
    private List<SimpleUserCardDTO> cards;
}
