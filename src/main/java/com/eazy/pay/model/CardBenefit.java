package com.eazy.pay.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "card_benefits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CardBenefit extends BaseEntity {

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩: 필요할 때만 데이터를 가져온다.
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @Column(name = "benefit_rate")
    private Integer benefitRate;
}
