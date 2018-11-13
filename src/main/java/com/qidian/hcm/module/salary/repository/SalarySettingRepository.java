/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 薪资配置表-SalaryConfig
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.SalarySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 薪资配置表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface SalarySettingRepository extends JpaRepository<SalarySetting, Long> {

    Optional<SalarySetting> findByKey(String key);
}
