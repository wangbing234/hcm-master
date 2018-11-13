/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 员工银行卡信息-EmployeeBankInfo
 * @date 2018-09-06 11:19:49
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.EmployeeBankInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 员工银行卡信息dao层接口类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public interface EmployeeBankInfoRepository extends JpaRepository<EmployeeBankInfo, Long> {

    Optional<EmployeeBankInfo> findByEmployeeId(Long employeeId);
}
