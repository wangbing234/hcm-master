package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.EmployeePosition;
import com.qidian.hcm.module.employee.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * 员工岗位表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, Long>,
        JpaSpecificationExecutor<EmployeePosition> {

    List<EmployeePosition> findAllByEmployeeIdOrderByStartDateDesc(Long id);

    @Modifying
    @Query(value = "update EmployeePosition set leaderId = :leaderId where leaderId = :employeeId")
    void updateLeaderId(@Param("employeeId") Long employeeId, @Param("leaderId") Long leaderId);

    List<EmployeePosition> findAllByPositionIdIn(List<Long> positionIds);

    List<EmployeePosition> findAllByEmployeeIdIn(List<Long> employeeIds);

    @Query(value = "SELECT ep FROM EmployeePosition ep INNER JOIN Employee e ON ep.employeeId = e.id " +
            "and e.status = :status ")
    List<EmployeePosition> findAllByEmployeeStatus(@Param("status") EmployeeStatus employeeStatus);

    @Query(value = "SELECT ep FROM EmployeePosition ep INNER JOIN Employee e ON ep.employeeId = e.id " +
            "and e.status <> :status ")
    List<EmployeePosition> findAllByExcludeEmployeeStatus(@Param("status") EmployeeStatus employeeStatus);

    void deleteAllByEmployeeId(Long employeeId);

    List<EmployeePosition> findAllByEmployeeId(Long employeeId);
}
