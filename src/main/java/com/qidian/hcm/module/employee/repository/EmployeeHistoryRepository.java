package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {

    List<EmployeeHistory> findAllByEmployeeIdOrderByCreatedDateDesc(Long id);
}
