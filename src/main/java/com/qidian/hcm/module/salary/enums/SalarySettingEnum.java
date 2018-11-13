package com.qidian.hcm.module.salary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SalarySettingEnum {

    FIRST_CYCLE_MONTH("首次薪资核算开始月份", "first_cycle_month"),
    START_CYCLE_DAY("计薪周期开始日期", "start_cycle_day"),
    CURRENT_CYCLE_MONTH("当前薪资核算月份", "current_cycle_month"),
    PAY_DATE("发薪日期", "salary_pay_date"),
    PAY_MONTH("发薪日期月份", "salary_pay_month");

    private String name;
    private String key;
}
