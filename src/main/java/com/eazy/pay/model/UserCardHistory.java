package com.eazy.pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "user_card_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserCardHistory extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_card_id", referencedColumnName = "uid")
    private UserCard userCard;
    private Date yearAndMonth; //sql.Date
    private Integer benefitAmount;
    private Integer useAmount;
    private Boolean is_fulfilled;
}
