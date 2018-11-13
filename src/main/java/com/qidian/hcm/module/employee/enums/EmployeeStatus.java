package com.qidian.hcm.module.employee.enums;

import lombok.Getter;

/**
 * 员工状态
 */
@Getter
public enum EmployeeStatus {
    /**
     * 试用
     */
    probation,
    /**
     * 正式
     */
    formal,
    /**
     * 离职
     */
    former
}
