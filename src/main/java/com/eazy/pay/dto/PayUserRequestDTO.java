package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayUserRequestDTO {
    Long userId;
    Long cardId;
    Long categoryId;
    Integer price;
    String storeCode;
    String storeName;
}
