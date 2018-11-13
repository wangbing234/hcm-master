package com.qidian.hcm.module.employee.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lyn
 * @date 2018/9/11 16 19
 */
@Getter
@Setter
public class EmployeeBaseInfoResponse {
    private Long id;
    private String name;
    private String avatar;
    private String mobile;
    private boolean superAdmin;
}
