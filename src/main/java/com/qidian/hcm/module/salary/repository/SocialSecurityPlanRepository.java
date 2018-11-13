/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 社保方案-SocialSecurityPlan
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.SocialSecurityPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 社保方案dao层接口类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public interface SocialSecurityPlanRepository extends JpaRepository<SocialSecurityPlan, Long> {


    /**
     * 查询重复的名称
     */
    Optional<SocialSecurityPlan> findByNameAndIdNot(String name, Long id);

}
