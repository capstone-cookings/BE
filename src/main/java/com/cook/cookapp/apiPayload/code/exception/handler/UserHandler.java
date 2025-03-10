package com.cook.cookapp.apiPayload.code.exception.handler;

import com.cook.cookapp.apiPayload.code.BaseErrorCode;
import com.cook.cookapp.apiPayload.code.exception.GeneralException;

public class UserHandler extends GeneralException {

    public UserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
