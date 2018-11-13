package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.employee.entity.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "employee_salary_monthly")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工月度薪资表数据实体类
 */
public class EmployeeSalaryMonthly implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 员工Id
     */
    @Column(name = "employee_id")
    @NotNull
    private Long employeeId;

    /**
     * 是否计薪 true计薪，false 不计薪
     */
    @Column(name = "include")
    private Boolean include;

    /**
     * 统计月份yyyyMM
     */
    @Column(name = "date")
    @NotNull(message = "统计月份yyyy-MM不能为空！")
    private String date;

    /**
     * 薪资计算结果
     */
    @Column(name = "items_result")
    private String itemsResult;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    private Employee employee;

}