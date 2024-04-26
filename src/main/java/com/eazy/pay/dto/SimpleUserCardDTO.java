package com.eazy.pay.dto;

import com.eazy.pay.model.Card;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SimpleUserCardDTO {
    private Card card;
    private int benefitAmount;
    private int useAmount;
    private int paymentLimit;
    private boolean linkEazy;
    private String num;
}
