package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工合同表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, Long> {

    List<EmployeeContract> findAllByEmployeeId(Long id);

    void deleteAllByEmployeeId(Long employeeId);
}
