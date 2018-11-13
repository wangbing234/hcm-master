package com.qidian.hcm.module.employee.entity;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "employee_work_experience")
@Getter
@Setter
@ApiModel(value = "employee_work_experience")
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工工作信息表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeWorkExperience implements Serializable {

    private static final long serialVersionUID = 2569003165640396845L;
    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 员工ID
     */
    @Column(name = "employee_id")
    private Long employeeId;
    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private Date startTime;
    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private Date endTime;
    /**
     * 工作单位
     */
    @Column(name = "work_unit")
    private String workUnit;
    /**
     * 任职岗位
     */
    @Column(name = "job_position")
    private String jobPosition;
    /**
     * 离职原因
     */
    @Column(name = "leave_reason")
    private String leaveReason;


}