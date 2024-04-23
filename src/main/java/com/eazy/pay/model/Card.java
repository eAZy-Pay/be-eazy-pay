package com.eazy.pay.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
/*롬복 @Data : Getter 및 Setter 메서드: 클래스의 필드에 대한 Getter 및 Setter 메서드를 생성합니다.
toString(): 클래스의 모든 필드를 포함하는 문자열 표현을 생성합니다.
equals() 및 hashCode(): 객체의 동등성을 비교하기 위한 equals() 메서드와 객체를 해시맵과 같은 자료 구조에 사용하기 위한 hashCode() 메서드를 생성합니다.
기본 생성자: 클래스의 기본 생성자를 생성합니다.
All-args 생성자: 클래스의 모든 필드를 매개변수로 받는 생성자를 생성합니다.
이렇게 자동으로 생성된 메서드들은 보일러플레이트 코드를 줄여주고 코드를 간결하게 작성할 수 있도록 도와줍니다. 따라서 @Data 어노테이션을 사용하면 간단한 POJO(Plain Old Java Object) 클래스를 빠르게 작성할 수 있습니다.
EqualsAndHashCode(callSuper = true) : 상속받은 클래스의 필드까지 동일한지 비교할 수 있도록 해줍니다.
@Builder : 빌더 패턴을 사용할 수 있도록 해줍니다.
* */
public class Card extends BaseEntity {

    private String image;

    private String name;

    @Column(name = "annual_fee")
    private Integer annualFee;

    @Column(name = "benefit_limit")
    private Integer benefitLimit;

    private String info;

    private Integer performance;

    @JsonManagedReference
    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY) // 지연로딩: 필요할 때만 데이터를 가져온다.
    private List<CardBenefit> benefitList;
}
