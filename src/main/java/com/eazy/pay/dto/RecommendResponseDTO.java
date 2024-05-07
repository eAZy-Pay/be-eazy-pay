package com.eazy.pay.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecommendResponseDTO {
    // 추천 카드 목록
    List<CardWithBenefitDTO> recommendList;
    // 상위 3개 카테고리에 속하는 보유한 카드 목록
    List<CardWithBenefitAndUsageDTO> userTop3CategoryCardList;
    // 사용금액 top 3 카드
    List<CardWithBenefitAndUsageDTO> userTop3UseAmountCardList;
    // top3 카테고리 사용금액
    List<UserCategoryHistoryDTO> userTop3CategoryUseAmountList;
    // 3개월 사용금액
    int totalUseAmount;
}
