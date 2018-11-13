package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.employee.entity.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "employee_financial")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工财务表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeFinancial implements Serializable {

    private static final long serialVersionUID = 895958804587817183L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 员工Id
     */
    @Column(name = "employee_id")
    private Long employeeId;
    /**
     * 薪酬
     */
    @Column(name = "salary")
    private Double salary;

    /**
     * 薪酬调整时间
     */
    @Column(name = "salary_adjust_date")
    private Date salaryAdjustDate;

    /**
     * 免税额类型
     */
    @Column(name = "threshold_id")
    private Long thresholdId;
    /**
     * 社保方案
     */
    @Column(name = "social_security_plan_id")
    private Long socialSecurityPlanId;
    /**
     * 社保基数
     */
    @Column(name = "social_security_base")
    private Double socialSecurityBase;
    /**
     * 公积金方案
     */
    @Column(name = "housing_fund_plan_id")
    private Long housingFundPlanId;
    /**
     * 公积金基数
     */
    @Column(name = "housing_fund_base")
    private Double housingFundBase;
    /**
     * 公积金账号
     */
    @Column(name = "housing_fund_account")
    private String housingFundAccount;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_time", insertable = false)
    private Date updateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    private Employee employee;

}