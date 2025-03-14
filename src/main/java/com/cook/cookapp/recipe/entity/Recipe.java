//package com.cook.cookapp.recipe.entity;
//
//
//
//import com.cook.cookapp.global.BaseEntity;
//import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
//import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
//import com.cook.cookapp.ingredient.entity.Enum.StorageType;
//import com.cook.cookapp.user.entity.User;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//import java.time.LocalDate;
//
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Builder
//public class Recipe extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // 레시피 제목
//    @Column(length = 100, nullable = false)
//    @NotNull
//    private String title;
//
//    // 레시피 설명
//    @Column(length = 255, nullable = false)
//    @NotNull
//    private String description;
//
//    // 요리 방법 (순서대로 적힌 레시피)
//    @Column(length = 500, nullable = false)
//    @NotNull
//    private String instructions;
//
//    // 재료 목록 (이 부분을 별도로 관리할 경우 중간 테이블 사용 가능)
//    @ElementCollection
//    private List<String> ingredients;  // 예: ["소금 2g", "밀가루 100g"]
//
//    // 소비기한 (날짜만 사용)
//    @Column(nullable = true)
//    private LocalDate useByDate;
//
//    // 수량
//    @Column(nullable = true)
//    private int count;
//
//    // 알림 상태 (ON, OFF) - 기본값 ON.
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AlarmStatus alarmStatus = AlarmStatus.ON;
//
//    // 저장 타입 (냉장, 냉동) - 기본값 냉장.
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private StorageType storageType = StorageType.REFRIGERATED;
//
//    // 레시피 작성자 (유저)
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//}