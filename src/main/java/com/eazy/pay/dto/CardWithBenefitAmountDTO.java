package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardWithBenefitAmountDTO {
    private CardDTO card;
    private Long categoryId;
    private String categoryName;
    private Boolean isOwned;
    private Integer benefitRate;
}
