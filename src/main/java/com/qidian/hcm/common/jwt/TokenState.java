package com.qidian.hcm.common.jwt;

import lombok.Getter;

/**
 * TokenState 用户登录状态
 *
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum TokenState {
    /**
     * 过期
     */
    EXPIRED("EXPIRED"),
    /**
     * 无效(token不合法)
     */
    INVALID("INVALID"),
    /**
     * 有效的
     */
    VALID("VALID");

    private String state;

    TokenState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.state;
    }
}
