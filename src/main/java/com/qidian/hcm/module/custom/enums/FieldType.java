package com.qidian.hcm.module.custom.enums;

import lombok.Getter;

/**
 * 控件类型
 */
@Getter
public enum FieldType {
    textField("textField", "文本框"),
    textarea("textarea", "多行文本框"),
    decimal("decimal", "数字框"),
    select("select", "下拉单选框"),
    checkbox("checkbox", "多选框"),
    dateRange("dateRange", "日期区间"),
    date("date", "日期"),
    file("file", "附件"),
    multiSelect("multiSelect", "下拉复选框"),
    radio("radio", "单选框");

    private String code;
    private String name;

    FieldType(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
