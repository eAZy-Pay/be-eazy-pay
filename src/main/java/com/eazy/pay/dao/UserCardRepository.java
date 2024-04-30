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

    // UserCard 정보 조회: 순수 JPA 메서드 사용하면 이달의 첫 번째 결제에서 혜택 우선순위 선택이 제대로 안됨
    @Query("SELECT uc FROM UserCard uc WHERE uc.user.uid = :userId")
    List<UserCard> findByUserId(@Param("userId") Long userId);
    @Query("SELECT uc FROM UserCard uc WHERE uc.user.uid = :uid")
    Optional<UserCard> findByUserCardUid(Long uid);

    List<UserCard> findByUserUid(Long userUid);

    // PaymentHistory 정보 조회
    @Query("SELECT new com.eazy.pay.dto.PaymentHistoryDTO(pb.paymentDate, pb.paymentAmount, pb.storeName, pb.cardNum) " +
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

    List<UserCard> findByUserUidAndCardValid(Long userUid, boolean cardValid);
}
