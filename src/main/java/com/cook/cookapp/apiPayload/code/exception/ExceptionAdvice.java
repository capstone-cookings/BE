package com.cook.cookapp.apiPayload.code.exception;

import com.cook.cookapp.apiPayload.ApiResponse;
import com.cook.cookapp.apiPayload.code.ErrorReasonDTO;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), errorMessage, null));
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(GeneralException ex) {
        ErrorReasonDTO errorReason = ex.getErrorReason();
        return ResponseEntity.status(errorReason.getHttpStatus()).body(
                ApiResponse.onFailure(errorReason.getCode(), errorReason.getMessage(), null)
        );
    }
}
