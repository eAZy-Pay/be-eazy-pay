package com.eazy.pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "highlighted_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HighlightedCard extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "uid")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "event_category_id", referencedColumnName = "uid")
    private EventCategory eventCategory;

    private Integer displayOrder;
}
