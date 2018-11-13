package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeWorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工工作信息表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeWorkExperienceRepository extends JpaRepository<EmployeeWorkExperience, Long> {

    List<EmployeeWorkExperience> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
