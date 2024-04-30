package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardWithBenefitAndUsageDTO {
    private CardWithBenefitDTO cardWithBenefitDTO;
    private Integer useAmount;
    private Integer benefitAmount;
}
