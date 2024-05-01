package com.eazy.pay.service;

import com.eazy.pay.dao.CardBenefitRepository;
import com.eazy.pay.dao.CategoryRepository;
import com.eazy.pay.dao.UserCardHistoryRepository;
import com.eazy.pay.dao.UserCategoryHistoryRepository;
import com.eazy.pay.dto.CardWithBenefitAndUsageDTO;
import com.eazy.pay.dto.CardWithBenefitDTO;
import com.eazy.pay.dto.RecommendResponseDTO;
import com.eazy.pay.dto.UserCategoryHistoryDTO;
import com.eazy.pay.mapper.CardWithBenefitMapper;
import com.eazy.pay.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private UserCardHistoryRepository userCardHistoryRepository;

    @Autowired
    private UserCategoryHistoryRepository userCategoryHistoryRepository;

    @Autowired
    private CardBenefitRepository cardBenefitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private RecommendResponseDTO notEnoughData() {
        List<UserCategoryHistoryDTO> top3Categories = categoryRepository.findAll().stream()
                .map(category -> UserCategoryHistoryDTO.builder()
                        .categoryId(category.getUid())
                        .categoryName(category.getName())
                        .useAmount(0)
                        .benefitAmount(0)
                        .build())
                .limit(3)
                .toList();

        List<CardWithBenefitDTO> recommendCategoryCard = cardBenefitRepository.findByCategoryUidIn(top3Categories.stream().map(UserCategoryHistoryDTO::getCategoryId).toList()).stream()
                .sorted((a, b) -> b.getBenefitRate() - a.getBenefitRate())
                .collect(Collectors.groupingBy(CardBenefit::getCategory))
                .values().stream()
                .flatMap(cardBenefitList -> cardBenefitList.stream().limit(1))
                .sorted(Comparator.comparingInt(cardBenefit -> top3Categories.indexOf(cardBenefit.getCategory())))
                .map(CardBenefit::getCard)
                .map(CardWithBenefitMapper.INSTANCE::toDTO)
                .toList();

        return RecommendResponseDTO.builder()
                .recommendList(recommendCategoryCard)
                .userTop3CategoryCardList(new ArrayList<>())
                .userTop3UseAmountCardList(new ArrayList<>())
                .userTop3CategoryUseAmountList(top3Categories)
                .build();
    }

    public RecommendResponseDTO getRecommendation() {
        return notEnoughData();
    }

    // 추천 서비스
    public RecommendResponseDTO getRecommendation(Long userId) {

        Date now = Date.valueOf(LocalDate.now().withDayOfMonth(1)); // 현재 월의 첫째 날
        Date threeMonthsAgo = Date.valueOf(now.toLocalDate().minusMonths(3)); // 3개월 전

        // [0] : 사용 금액, [1] : 혜택 금액

        // 카드별 사용 내역 집계
        List<UserCardHistory> userCardHistoryList = userCardHistoryRepository.findByUserUid(userId).stream()
                .filter(h -> (h.getYearAndMonth().after(threeMonthsAgo) || h.getYearAndMonth().equals(threeMonthsAgo)) && h.getYearAndMonth().before(now))
                .toList();

        Map<UserCard, Integer[]> cardUsageSum = userCardHistoryList.stream()
                .collect(Collectors.groupingBy(UserCardHistory::getUserCard,
                        Collectors.reducing(new Integer[]{0, 0},
                                h -> new Integer[]{h.getUseAmount(), h.getBenefitAmount()},
                                (a, b) -> new Integer[]{a[0] + b[0], a[1] + b[1]})));

        // 카테고리별 사용 내역 집계
        List<UserCategoryHistory> userCategoryHistoryList = userCategoryHistoryRepository.findByUserId(userId).stream()
                .filter(h -> (h.getYearAndMonth().after(threeMonthsAgo) || h.getYearAndMonth().equals(threeMonthsAgo)) && h.getYearAndMonth().before(now))
                .toList();

        Map<Category, Integer[]> categoryUsageSum = userCategoryHistoryList.stream()
                .collect(Collectors.groupingBy(UserCategoryHistory::getCategory,
                        Collectors.reducing(new Integer[]{0, 0},
                                h -> new Integer[]{h.getUseAmount(), h.getBenefitAmount()},
                                (a, b) -> new Integer[]{a[0] + b[0], a[1] + b[1]})));

        if(categoryUsageSum.isEmpty() || cardUsageSum.isEmpty()) {
            return notEnoughData();
        }

        // 사용금액 상위 3개 카테고리
        List<UserCategoryHistoryDTO> top3Categories = categoryUsageSum.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue()[0]))
                .limit(3)
                .map(Map.Entry::getKey)
                .map(category -> {
                    Integer[] usage = categoryUsageSum.get(category);
                    return UserCategoryHistoryDTO.builder()
                            .categoryId(category.getUid())
                            .categoryName(category.getName())
                            .useAmount(usage[0])
                            .benefitAmount(usage[1])
                            .build();
                })
                .toList();


         /**
          * 1. 카테고리별 상위 3개 카테고리 추천 카드
          * 2. 소유한 카드가 아닌 카드 중에서 카테고리별 benefitRate가 높은 카드 추천
          * 3. 카테고리별 하나씩 남김
          */
        List<CardWithBenefitDTO> recommendCategoryCard = cardBenefitRepository.findByCategoryUidIn(top3Categories.stream().map(UserCategoryHistoryDTO::getCategoryId).toList()).stream()
                .sorted((a, b) -> b.getBenefitRate() - a.getBenefitRate())
                .filter(cardBenefit -> userCardHistoryList.stream().noneMatch(userCardHistory -> userCardHistory.getUserCard().getCard().equals(cardBenefit.getCard())))
                .collect(Collectors.groupingBy(CardBenefit::getCategory))
                .values().stream()
                .flatMap(cardBenefitList -> cardBenefitList.stream().limit(1))
                .sorted(Comparator.comparingInt(cardBenefit -> top3Categories.indexOf(cardBenefit.getCategory())))
                .map(CardBenefit::getCard)
                .map(CardWithBenefitMapper.INSTANCE::toDTO)
                .toList();


        //  카드 사용 내역 중 혜택 카테고리가 top3에 속하는 카드 찾기
        List <CardWithBenefitAndUsageDTO> top3CategoryCard = cardUsageSum.entrySet().stream()
                .filter(e -> e.getKey().getCard().getBenefitList().stream().anyMatch(b -> top3Categories.contains(b.getCategory())))
                .map(Map.Entry::getKey)
                .map(UserCard::getCard)
                .map(card -> {
                    Integer[] usage = cardUsageSum.get(card);
                    return CardWithBenefitAndUsageDTO.builder()
                            .cardWithBenefitDTO(CardWithBenefitMapper.INSTANCE.toDTO(card))
                            .useAmount(usage[0])
                            .benefitAmount(usage[1])
                            .build();
                })
                .toList();

//      많이 쓴 카드
        List<CardWithBenefitAndUsageDTO> top3UsedCard = cardUsageSum.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue()[0]))
                .limit(3)
                .map(Map.Entry::getKey)
                .map(UserCard::getCard)
                .map(card -> {
                    Integer[] usage = cardUsageSum.get(card);
                    return CardWithBenefitAndUsageDTO.builder()
                            .cardWithBenefitDTO(CardWithBenefitMapper.INSTANCE.toDTO(card))
                            .useAmount(usage[0])
                            .benefitAmount(usage[1])
                            .build();
                })
                .toList();



        return RecommendResponseDTO.builder()
                .recommendList(recommendCategoryCard)
                .userTop3CategoryCardList(top3CategoryCard)
                .userTop3UseAmountCardList(top3UsedCard)
                .userTop3CategoryUseAmountList(top3Categories)
                .build();
    }
}
