package com.qidian.hcm.module.salary.utils;

public class SalaryFormulaIKeyword {

    private SalaryFormulaIKeyword() {
    }

    //社保
    public static final String SOCIAL_SECURITY = SalaryItemsConst.EXTERNAL_EMPLOYEE + ".socialSecurity";

    //公积金
    public static final String HOUSING_FUND = SalaryItemsConst.EXTERNAL_EMPLOYEE + ".housingFund";

    //起征点
    public static final String THRESHOLD = SalaryItemsConst.EXTERNAL_EMPLOYEE + ".threshold";

    //薪资的编码
    public static final String SALARY_CODE = "salary";

}
