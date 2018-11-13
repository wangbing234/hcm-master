package com.qidian.hcm.module.salary.enums;

public enum SalaryType {
    taxBeforeAdd,//税前加项
    taxBeforeMinus,//税前减项
    taxAfterAdd,//税后加项
    taxAfterMinus,//税后减项
    external,//不直接参与薪资计算项
    laborCost;//人力成本项
}
