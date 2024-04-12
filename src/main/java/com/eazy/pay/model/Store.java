package com.eazy.pay.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Store extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "uid")
    private Category category;

    @Column(name = "name")
    private String name;
}
