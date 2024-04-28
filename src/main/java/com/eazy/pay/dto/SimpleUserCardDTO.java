package com.eazy.pay.dto;

import com.eazy.pay.model.Card;
import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SimpleUserCardDTO {
    private Long uid;
    private String num;
    private int benefitAmount;
    private int useAmount;
    private int paymentLimit;
    private Date expirationDate;
    private boolean cardValid;
    private boolean linkEazy;
    private Card card;
}
