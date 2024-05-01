package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPayCardDTO {
    private Long cardId;
    private Integer payback;
    private String cardName;
    private String cardImage;

}
