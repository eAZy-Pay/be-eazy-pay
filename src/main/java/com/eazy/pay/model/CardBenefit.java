package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_benefits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CardBenefit extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card cardId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category categoryId;

    @Column(name = "benefit_rate")
    private Integer benefitRate;
}
