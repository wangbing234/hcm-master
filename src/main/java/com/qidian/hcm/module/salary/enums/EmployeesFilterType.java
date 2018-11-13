package com.qidian.hcm.module.salary.enums;

import lombok.Getter;

@Getter
public enum EmployeesFilterType {

    all("all"),//全部
    hire("hire_date"),//入职
    adjust("salary_adjust_date"),//调薪
    resignation("resignation_date");//离职

    private String dataBaseCode;

    EmployeesFilterType(String dataBaseCode) {
        this.dataBaseCode = dataBaseCode;
    }
}
