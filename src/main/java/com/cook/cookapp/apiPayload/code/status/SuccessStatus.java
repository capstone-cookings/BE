package com.cook.cookapp.apiPayload.code.status;

import com.cook.cookapp.apiPayload.code.BaseCode;
import com.cook.cookapp.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공!"),

    // 성공 관련 응답
    SUCCESS_GET_NOTICE_LIST(HttpStatus.OK, "COMMON2001", "공지사항 목록 읽기 성공"),
    SUCCESS_FETCH_NOTICE_UPDATE(HttpStatus.OK, "COMMON2002", "공지사항 수정 성공"),
    SUCCESS_GET_NOTICE(HttpStatus.OK, "COMMON2002", "공지사항 읽기 성공"),
    NO_DUPLICATE_NICKNAME(HttpStatus.OK, "COMMON2002","닉네임 생성이 가능"),
    SUCCESS_POST_RECIPE(HttpStatus.OK, "COMMON2002", "레시피 저장 완료");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

//    @Override
//    public ReasonDTO getReason() {
//        return ReasonDTO.builder()
//                .message(message)
//                .code(code)
//                .isSuccess(true)
//                .build();
//    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}