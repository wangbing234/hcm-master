package com.qidian.hcm.module.employee.entity;

import com.qidian.hcm.module.employee.enums.IdentityType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "employee_identity")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工证件表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeIdentity implements Serializable {

    private static final long serialVersionUID = -1012536434746003622L;
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
     *
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private IdentityType type;
    /**
     *
     */
    @Column(name = "code")
    private String code;
    /**
     * 自定义字段
     */
    @Column(name = "customized_field")
    private String customizedField;


}