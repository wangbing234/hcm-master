package com.qidian.hcm.module.employee.enums;

import lombok.Getter;

@Getter
public enum EmployeeHistoryTitle {
    QUALIFY("qualify", "员工转正"),
    TRANSFER("transfer", "员工调岗"),
    RESIGN("resign", "员工离职");

    private String code;
    private String name;

    EmployeeHistoryTitle(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
