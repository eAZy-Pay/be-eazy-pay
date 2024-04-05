package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Question extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private User user;

    private Date date;

    private String title;

    private String content;

    @Column(name = "is_answered")
    private boolean isAnswered;

    private String answer;
}
