package com.qidian.hcm.module.employee.entity;

import com.alibaba.fastjson.JSONObject;
import com.qidian.hcm.module.employee.enums.ContractPeriod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "employee_contract")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工合同表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeContract implements Serializable {

    private static final long serialVersionUID = -3738206118720802936L;
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
     * 签约单位
     */
    @Column(name = "sign_unit")
    @ApiModelProperty(value = "签约单位", name = "sign_unit", required = true)
    private String signUnit;
    /**
     *
     */
    @Column(name = "start_date")
    private Date startDate;
    /**
     *
     */
    @Column(name = "end_date")
    private Date endDate;
    /**
     * 期限
     */
    @Column(name = "period")
    @Enumerated(EnumType.STRING)
    private ContractPeriod period;
    /**
     * 试用期结束日期
     */
    @Column(name = "probation_end_date")
    private Date probationEndDate;
    /**
     *
     */
    @Column(name = "customized_field")
    private String customizedField;

    @Transient
    private JSONObject customizedFieldJson;
}