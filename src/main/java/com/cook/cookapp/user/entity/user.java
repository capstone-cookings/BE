package com.cook.cookapp.user.entity;

import com.cook.cookapp.global.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class user extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 이름
    @Column(length = 20, nullable = false)
    @NotNull
    private String name;

    //사용자 닉네임
    @Column(length = 20, unique = true)
    private String nickname;

    //사용자 이메일
    @Column(length = 100, unique = true)
    private String email;
}
