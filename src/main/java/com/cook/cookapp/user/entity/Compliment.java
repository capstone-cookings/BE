package com.cook.cookapp.user.entity;

import com.cook.cookapp.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compliment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplimentType complimentType;

    // 칭찬한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complimenter_id")
    private User complimenter;

    // 칭찬받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complimented_user_id")
    private User complimentedUser;

}
