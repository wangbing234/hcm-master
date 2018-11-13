package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.salary.enums.SalaryAjustType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "salary_history")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工调薪记录表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class SalaryHistory implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 雇员id
     */
    @Column(name = "employee_id")
    private Long employeeId;
    /**
     * 调薪前薪资
     */
    @Column(name = "before_adjust")
    private Double beforeAdjust;
    /**
     * 调薪后薪资
     */
    @Column(name = "after_adjust")
    private Double afterAdjust;
    /**
     * 调薪时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "adjust_date", updatable = false)
    private Date adjustDate;

    /**
     * 调薪类型，现在默认是basic
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SalaryAjustType type;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 涨幅
     */
    @Column(name = "increased")
    private Double increased;


}