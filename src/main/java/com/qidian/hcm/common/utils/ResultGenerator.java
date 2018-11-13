package com.qidian.hcm.common.utils;

/**
 * 响应结果生成工具
 */
public final class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    private ResultGenerator() {
    }

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static Result genSuccessResult(Object data) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static Result genFailResult(ResultCode resultCode) {
        return new Result()
                .setCode(resultCode.getCode())
                .setMessage(resultCode.getMsg());
    }

    public static Result genFailResult(String code, String message) {
        return new Result()
                .setCode(code)
                .setMessage(message);
    }

}
