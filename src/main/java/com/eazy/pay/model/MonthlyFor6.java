package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "monthly_for_6")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MonthlyFor6 extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    @Column(name = "use_amount")
    private int useAmount;

}
