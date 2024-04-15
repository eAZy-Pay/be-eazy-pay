package com.eazy.pay.dto;

import com.eazy.pay.model.Card;
import com.eazy.pay.model.PayBenefit;
import lombok.*;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SimpleUserCardDTO {
    private int benefitAmount;
    private List<Card> cards;
}
