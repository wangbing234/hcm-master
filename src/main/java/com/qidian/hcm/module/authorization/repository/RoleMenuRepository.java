package com.qidian.hcm.module.authorization.repository;

import com.qidian.hcm.module.authorization.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {

    void deleteAllByRoleId(Long roleId);
}