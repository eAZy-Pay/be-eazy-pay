package com.eazy.pay.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QnaDTO {
    private Long uid;
    private String date;
    private String title;
    private String content;
    private Boolean isAnswered;
    private String answer;
    private Long userId;
    private String userName;
}
