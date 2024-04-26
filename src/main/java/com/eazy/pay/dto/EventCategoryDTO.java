package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventCategoryDTO {
    private Long uid;
    private String name;
}
