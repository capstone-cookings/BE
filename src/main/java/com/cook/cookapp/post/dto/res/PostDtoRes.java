package com.cook.cookapp.post.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDtoRes {
    private Long id;
    private String foodName;
    private Long useByDate;
    private int count;
    private boolean storageType;
    private boolean alarmStatus;

}
