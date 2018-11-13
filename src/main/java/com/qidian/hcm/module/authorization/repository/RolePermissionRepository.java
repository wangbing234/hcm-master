package com.qidian.hcm.module.authorization.repository;

import com.qidian.hcm.module.authorization.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    void deleteAllByRoleId(Long roleId);

    List<RolePermission> findAllByRoleId(Long roleId);

    List<RolePermission> findAllByRoleIdIn(Collection<Long> roleIds);
}