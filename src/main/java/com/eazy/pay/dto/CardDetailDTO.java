package com.eazy.pay.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardDetailDTO {
    private Long uid;
    private String image;
    private String name;
    private Integer annualFee;
    private Integer performance;
    private Integer benefitLimit;
    private String info;
    private String applicationUrl;
    private List<CardDetailBenefitDTO> benefitList;
}
