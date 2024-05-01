package com.eazy.pay.dto;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidUserCardDTO {
    private Long uid;
    private Long userId;
    private Long cardId;
    private String image;
    private String cardName;
//    private int expectedBenefit;
}
