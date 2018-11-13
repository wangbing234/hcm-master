package com.qidian.hcm.module.employee.entity;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "employee_emergency_contact")
@Getter
@Setter
@ApiModel(value = "employee_emergency_contact")
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工紧急联系人表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeEmergencyContact implements Serializable {

    private static final long serialVersionUID = -3741323447282951940L;
    /**
     * 主键
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
     * 姓名
     */
    @Column(name = "name")
    private String name;
    /**
     * 关系
     */
    @Column(name = "relationship")
    private String relationship;
    /**
     * 手机
     */
    @Column(name = "mobile")
    private String mobile;


}