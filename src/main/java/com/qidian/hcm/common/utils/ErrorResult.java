package com.qidian.hcm.common.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResult {
    private final String code;
    private final String message;
    private final Object error;

    public ErrorResult(ResultCode resultCode, Object error) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.error = error;
    }

}
