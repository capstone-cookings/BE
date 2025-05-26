package com.cook.cookapp.user.dto.req;

import com.cook.cookapp.user.entity.Enum.ReportType;
import lombok.*;

public class ReportDtoReq {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportRequestDto {
        private ReportType reportType;
        private String content; // 선택 사항
    }

}
