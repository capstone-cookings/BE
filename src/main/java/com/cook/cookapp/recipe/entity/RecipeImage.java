package com.cook.cookapp.recipe.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeImage extends BaseEntity {

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

    @OneToOne(mappedBy = "recipeImage")
    private Recipe recipe;

}
