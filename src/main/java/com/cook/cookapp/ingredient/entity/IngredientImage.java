package com.cook.cookapp.ingredient.entity;


import com.cook.cookapp.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientImage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String originalFilename;

    @Column
    private String contentType;

    @Column
    private Long fileSize;

    @OneToOne(mappedBy = "ingredientImage")
    private Ingredient ingredient;

}
