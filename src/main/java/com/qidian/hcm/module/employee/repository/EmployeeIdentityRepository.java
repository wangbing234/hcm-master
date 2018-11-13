package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工证件表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeIdentityRepository extends JpaRepository<EmployeeIdentity, Long> {

    List<EmployeeIdentity> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
