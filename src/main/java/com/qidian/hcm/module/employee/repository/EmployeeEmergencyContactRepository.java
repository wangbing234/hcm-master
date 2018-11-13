package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工紧急联系人表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeEmergencyContactRepository extends JpaRepository<EmployeeEmergencyContact, Long> {

    List<EmployeeEmergencyContact> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
