package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.entity.CustomizedEmployeeForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 自定义员工表单表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface CustomizedEmployeeFormRepository extends JpaRepository<CustomizedEmployeeForm, Long> {

    @Modifying
    @Query("update CustomizedEmployeeForm u set  u.enable=:enable  where u.id=:id")
    void enabledCustomizedEmployeeForm(@Param("id") Long id, @Param("enable") Boolean enable);

    @Query(value = "select  ifnull(max(b.idx),0)  from customized_employee_form b where b.type=:type",
            nativeQuery = true)
    Integer findMaxIdxByType(@Param("type") String type);

    List<CustomizedEmployeeForm> findCustomizedEmployeeFormByTitle(String title);

    List<CustomizedEmployeeForm> findByEnableOrderByTypeAscIdxAsc(Boolean enable);

}
