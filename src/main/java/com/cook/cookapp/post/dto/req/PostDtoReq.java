package com.cook.cookapp.post.dto.req;

import com.cook.cookapp.post.entity.Enum.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostDtoReq {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수입니다.")
    private String content;

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

    @Min(value = 2) //2 이상 입력 받아야 함
    private int memberCount;

    @Positive(message = "가격은 양수여야 합니다.")
    private int price;
}
