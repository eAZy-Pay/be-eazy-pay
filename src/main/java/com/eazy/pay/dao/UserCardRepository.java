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

    List<UserCard> findByUserUid(Long userUid, Pageable pageable);

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
            Pageable pageable
    );

    boolean existsByNum(String num);
}
