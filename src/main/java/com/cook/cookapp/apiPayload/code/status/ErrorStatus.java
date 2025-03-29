package com.cook.cookapp.apiPayload.code.status;

import com.cook.cookapp.apiPayload.code.BaseErrorCode;
import com.cook.cookapp.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 에러 예시
    FAIL_OOOOO(HttpStatus.BAD_REQUEST, "FAIL", "실패하였습니다."),

    // 토큰 관련 에러
    JWT_AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "JWT4001", "권한이 없습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT4002", "유효하지 않은 토큰입니다."),
    JWT_EMPTY(HttpStatus.UNAUTHORIZED, "JWT4003", "JWT 토큰을 넣어주세요."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4004", "만료된 토큰입니다."),
    JWT_REFRESHTOKEN_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "JWT4005", "RefreshToken이 일치하지 않습니다."),
    JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4031", "리프레시 토큰이 존재하지 않거나 만료되었습니다."),
    JWT_REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT4032", "유효하지 않은 리프레시 토큰입니다."),

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // token
    TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "TOKEN_400_1", "헤더에 토큰이 존재하지 않음"),
    TOKEN_UNABLE_TO_EXTRACT(HttpStatus.BAD_REQUEST, "TOKEN_400_2", "토큰을 추출할 수 없음"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_401_1", "토큰이 만료됨"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_401_2", "토큰이 유효하지 않음"),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "TOKEN_401_3", "지원하지 않는 토큰 타입임"),
    ID_TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "TOKEN_400_3", "id_token 이 만료되었거나 유효하지 않음."),

    //User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "사용자를 찾을 수 없습니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임 중복입니다"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4003", "닉네임을 2자 이상, 20자 이하로 작성해주세요"),
    //Page
    INVALID_PAGE_PARAMETER(HttpStatus.BAD_REQUEST, "PAGE400", "잘못된 페이지 값입니다. 1 이상의 정수로 입력해주세요."),

    //식재료
    INGREDIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "INGREDIENT404", "식재료를 찾을 수 없습니다."),

    //Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4004", "게시글을 찾을 수 없습니다"),

    //Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE4004", "레시피를 찾을 수 없습니다"),
    ALREADY_LIKED_RECIPE(HttpStatus.BAD_REQUEST, "RECIPE4000", "레시피에 이미 좋아요를 눌렀습니다"),
    NOT_LIKED_YET(HttpStatus.BAD_REQUEST, "RECIPE4000","레시피에 좋아요를 누르지 않았습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "RECIPE4001","레시피의 소유자가 아닙니다"),

    //Chatbot
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "400", "유효하지 않은 입력 값입니다."),
    OPENAI_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI500", "AI 추천 서비스 호출 중 오류가 발생했습니다."),

    // Chat
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4001", "채팅방을 찾을 수 없습니다."),
    UNAUTHORIZED_CHAT_ACCESS(HttpStatus.FORBIDDEN, "CHAT4002", "해당 채팅방에 접근할 권한이 없습니다."),
    CHATROOM_FULL(HttpStatus.BAD_REQUEST, "CHAT4003", "채팅방 인원이 가득 찼습니다."),
    NOT_PARTICIPANT_CHATROOM(HttpStatus.BAD_REQUEST, "CHAT4004", "채팅방에 참여 중이지 않습니다."),
    CHATROOM_CLOSED(HttpStatus.BAD_REQUEST, "CHAT4005", "채팅방이 마감되었습니다."),
    CHATROOM_NOT_OWNER(HttpStatus.FORBIDDEN, "CHAT4006", "채팅방을 마감할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

//    @Override
//    public ErrorReasonDTO getReason() {
//        return ErrorReasonDTO.builder()
//                .message(message)
//                .code(code)
//                .isSuccess(false)
//                .build();
//    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
