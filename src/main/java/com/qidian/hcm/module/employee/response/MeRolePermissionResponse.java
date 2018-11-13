package com.qidian.hcm.module.employee.response;

import com.qidian.hcm.module.authorization.dto.RolePermissionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lyn
 * @date 2018/9/11 16 19
 */
@Getter
@AllArgsConstructor
public class MeRolePermissionResponse {
    private boolean superAdmin;
    private RolePermissionDTO permission;
}
