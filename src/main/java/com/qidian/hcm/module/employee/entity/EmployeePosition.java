package com.qidian.hcm.module.employee.entity;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.module.organization.entity.GradeEntity;
import com.qidian.hcm.module.organization.entity.OrganizationEntity;
import com.qidian.hcm.module.organization.entity.PositionEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "employee_position")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工岗位表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeePosition implements Serializable {

    private static final long serialVersionUID = -2230051914143511434L;
    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     *
     */
    @Column(name = "employee_id")
    private Long employeeId;
    /**
     * 开始时间
     */
    @Column(name = "start_date")
    private Date startDate;
    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private Long companyId;
    /**
     * 部门ID
     */
    @Column(name = "department_id")
    private Long departmentId;
    /**
     * 岗位ID
     */
    @Column(name = "position_id")
    private Long positionId;
    /**
     * 职级ID
     */
    @Column(name = "grade_id")
    private Long gradeId;
    /**
     * 直接主管
     */
    @Column(name = "leader_id")
    private Long leaderId;
    /**
     *
     */
    @Column(name = "customized_field")
    private String customizedField;

    @Transient
    private JSONObject customizedFieldJson;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    private PositionEntity position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    private OrganizationEntity department;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private OrganizationEntity company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", updatable = false, insertable = false)
    private GradeEntity grade;
}