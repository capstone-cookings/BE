package com.cook.cookapp.post.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.entity.Enum.Category;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category = Category.VEGETABLE;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String content;

    @Column(nullable = true)
    private int likeCount;

    @Column
    private int memberCount;

    @Column
    private int price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //TODO 사진,위치정보도 적기

    @PrePersist
    public void prePersist() {
        if (this.category == null) {
            this.category = Category.VEGETABLE;
        }
    }

    // 게시글 정보 업데이트 메서드
    public void update(PostDtoReq postDtoReq) {
        this.price = postDtoReq.getPrice();
        this.title = postDtoReq.getTitle();
        this.memberCount = postDtoReq.getMemberCount();
        this.category = postDtoReq.getCategory();
        this.content = postDtoReq.getContent();
    }
}
