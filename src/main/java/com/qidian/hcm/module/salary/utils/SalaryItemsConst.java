package com.qidian.hcm.module.salary.utils;

/**
 * 公式计算工具类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */

public class SalaryItemsConst {

    private SalaryItemsConst() {
    }

    public static final String METHOD_PREFIX = "#{";

    public static final String PREFIX = "${";

    public static final String SUFFIX = "}";

    public static final String EXTERNAL_EMPLOYEE = "employee";

    public static final String EXTERNAL_SERVICE = "service";

    public static final String PARAM_PATTERN = "(?<=(?<!\\\\)\\$\\{)(.*?)(?=(?<!\\\\)\\})";

}
