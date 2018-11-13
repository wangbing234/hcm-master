/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 员工月度薪资表-EmployeeSalaryMonthly
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.dto.EmployeeSalaryMonthlyDTO;
import com.qidian.hcm.module.salary.entity.EmployeeSalaryMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * 员工月度薪资表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeSalaryMonthlyRepository extends JpaRepository<EmployeeSalaryMonthly, Long>,
        JpaSpecificationExecutor<EmployeeSalaryMonthly> {

    /**
     * 查询员工当月明细
     *
     * @return
     */
    List<EmployeeSalaryMonthly> findByEmployeeIdInAndDate(Collection<Long> ids, String date);

    /**
     * 查询记薪的员工当月明细
     *
     * @return
     */
    List<EmployeeSalaryMonthly> findByEmployeeIdInAndDateAndIncludeIsTrue(List<Long> ids, String date);

    /**
     * 查询员工当月明细
     *
     * @return
     */
    Optional<EmployeeSalaryMonthly> findByEmployeeIdAndDate(Long employeeId, String date);

    /**
     * 根据月份分页查询
     */
    Page<EmployeeSalaryMonthly> findAllByDate(String date, Pageable page);

    @Query(value = "select new com.qidian.hcm.module.salary.dto.EmployeeSalaryMonthlyDTO(" +
            "esm.employeeId,e.employeeNo,e.name,esm.include,esm.itemsResult) from EmployeeSalaryMonthly  esm," +
            "com.qidian.hcm.module.employee.entity.Employee e where esm.employeeId=e.id")
    List<EmployeeSalaryMonthlyDTO> findAllByDate(String date);
}
