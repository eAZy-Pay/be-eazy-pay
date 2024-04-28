package com.eazy.pay.dao;

import com.eazy.pay.dto.PaymentHistoryDTO;
import com.eazy.pay.model.UserCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    List<UserCard> findAll();

    Optional<UserCard> findByUid(Long uid);

    List<UserCard> findByUserUid(Long userUid);

    /*
    *지연 로딩 또는 즉시 로딩: 순수 JPA 메서드는 관계 설정에 따라 지연 로딩이나 즉시 로딩을 다르게 처리할 수 있습니다.
    * @Query 메서드는 명시적으로 쿼리를 작성하기 때문에, 로딩 전략에 대한 제어가 가능합니다.
    *
    * 데이터 일관성: 특정 조건 또는 로직에 따라, 순수 JPA 메서드와 명시적 쿼리 간의 결과가 다를 수 있습니다.
    * 특히, 연관 관계가 복잡하거나 계산 로직이 포함된 경우에 이런 차이가 발생할 수 있습니다.
    * */
    // UserCard 정보 조회: 순수 JPA 메서드 사용하면 이달의 첫 번째 결제에서 혜택 우선순위 선택이 제대로 안됨
    @Query("SELECT uc FROM UserCard uc WHERE uc.user.uid = :userId")
    List<UserCard> findByUsertest(@Param("userId") Long userId);

    // PaymentHistory 정보 조회
    @Query("SELECT new com.eazy.pay.dto.PaymentHistoryDTO(pb.paymentDate, pb.paymentAmount, pb.storeName) " +
            "FROM UserCard uc " +
            "JOIN uc.user u " +
            "LEFT JOIN PaymentHistory pb ON pb.cardNum = uc.num " +
            "WHERE u.uid = :userId " +
            "AND YEAR(pb.paymentDate) = :year " +
            "AND MONTH(pb.paymentDate) = :month " +
            "ORDER BY pb.paymentDate DESC")
    Page<PaymentHistoryDTO> findPaymentHistoryByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month,
            Pageable pageable);

    boolean existsByNum(String num);
}
