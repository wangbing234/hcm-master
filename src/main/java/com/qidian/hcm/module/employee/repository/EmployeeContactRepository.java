package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工联系信息表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeContactRepository extends JpaRepository<EmployeeContact, Long> {

    List<EmployeeContact> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
