/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 员工调薪记录表-SalaryHistory
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 员工调薪记录表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    List<SalaryHistory> findByEmployeeId(Long employeeId);
}
