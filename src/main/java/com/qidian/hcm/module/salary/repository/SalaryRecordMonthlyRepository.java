/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 薪资项-SalaryItem
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.SalaryRecordMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 归档记录
 *
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
public interface SalaryRecordMonthlyRepository extends JpaRepository<SalaryRecordMonthly, Long> {

    /**
     * 根据计薪周期查询
     * @param cycle
     */
    Optional<SalaryRecordMonthly> findByCycle(String cycle);
}
