package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工教育信息表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, Long> {

    List<EmployeeEducation> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
