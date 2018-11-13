/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 员工财务表-EmployeeFinancial
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.EmployeeFinancial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * 员工财务表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeFinancialRepository extends JpaRepository<EmployeeFinancial, Long> {

    Optional<EmployeeFinancial> findByEmployeeId(Long employeeId);

    /**
     * 根据社保id查询
     */
    List<EmployeeFinancial> findBySocialSecurityPlanId(Long id);

    /**
     * 根据公积金id查询
     */
    List<EmployeeFinancial> findByHousingFundPlanId(Long id);

    /**
     * 根据起征点id查询
     */
    List<EmployeeFinancial> findByThresholdId(Long id);
}
