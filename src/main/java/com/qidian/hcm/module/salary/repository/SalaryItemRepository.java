/**
 * Powered By [rapid-framework]
 *
 * @author :wangbing
 * @Description: 薪资项-SalaryItem
 * @date 2018-09-06 11:19:50
 * @version V1.0
 **/

package com.qidian.hcm.module.salary.repository;

import com.qidian.hcm.module.salary.entity.SalaryItem;
import com.qidian.hcm.module.salary.enums.SalaryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * 薪资项dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface SalaryItemRepository extends JpaRepository<SalaryItem, Long> {

    Optional<SalaryItem> findByNameAndIdNot(String name, Long id);

    Optional<SalaryItem> findByCode(String code);

    List<SalaryItem> findByType(SalaryType salaryType);

    List<SalaryItem> findAllByCodeIn(Collection<String> codes);

    /**
     * 查找输入的薪资项（目前只有考勤）
     */
    List<SalaryItem> findAllByInflow(Boolean inflow);

    /**
     * 查询是否公式已经被引用
     */
    List<SalaryItem> findByFormulaLike(String name);


    List<SalaryItem> findByInOption(Boolean inOption);

    List<SalaryItem> findByInListOrderByIdAsc(Boolean inList);
}
