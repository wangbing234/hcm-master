package com.qidian.hcm.common.exception;

import com.qidian.hcm.common.utils.ResultCode;
import lombok.Getter;

/**
 * @Author lyn
 * @Date 2018/8/2 15 23
 */
@Getter
public class BizException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private Object data;

    public BizException(ResultCode resultCode) {
        super();
        this.errorCode = resultCode.getCode();
        this.message = resultCode.getMsg();
    }

    public BizException(ResultCode resultCode, Object data) {
        super();
        this.errorCode = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = data;
    }
}
