package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HighlightedCardDTO {
    private Long uid;
    private CardDTO card;
    private String eventCategoryId;
    private String eventCategoryName;
    private Integer displayOrder;
}
