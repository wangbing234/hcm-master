package com.qidian.hcm.utils;

public enum TokenType {
    MANAGER("管理员"), HAVE_PERMISSION("普通权限用户"), NO_PERMISSION("无权限用户");

    private String name;

    private TokenType(String name) {
        this.name = name;
    }
}
