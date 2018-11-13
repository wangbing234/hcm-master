package com.qidian.hcm.module.employee.repository;

import com.qidian.hcm.module.employee.dto.CommonListDTO;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * 员工表dao层接口类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    @Query(value = "select new com.qidian.hcm.module.employee.dto." +
            "CommonListDTO(b.id,b.name) " +
            "from  Employee b where b.status <>'former' order by b.createdDate desc")
    List<CommonListDTO> findEmployees();

    // 查询重复的员工编号
    List<Employee> findByEmployeeNoAndIdNot(String employeeNo, Long id);

    List<Employee> findByIdNotIn(Collection<Long> ids);

    Optional<Employee> findByEmployeeNo(String employeeNo);

    // 离职时查询直属leader是否存在
    Optional<Employee> findByIdAndStatus(Long id, EmployeeStatus status);

    Optional<Employee> findByIdAndStatusIsNot(Long id, EmployeeStatus status);

    Optional<Employee> findByUserId(Long userId);

    Page<Employee> findByNameLikeAndStatusOrderByIdDesc(String name, EmployeeStatus status, Pageable pageable);

    Page<Employee> findByStatusOrderByIdDesc(EmployeeStatus status, Pageable pageable);

    Page<Employee> findByNameLikeOrMobileLikeAndStatusNot(String name, String mobile, EmployeeStatus status,
                                                          Pageable pageable);

    Page<Employee> findByStatusNot(EmployeeStatus status, Pageable pageable);
}
