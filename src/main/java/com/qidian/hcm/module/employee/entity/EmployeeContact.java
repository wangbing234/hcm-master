package com.qidian.hcm.module.employee.entity;

import com.qidian.hcm.module.employee.enums.ContactType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "employee_contact")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工联系信息表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class EmployeeContact implements Serializable {

    private static final long serialVersionUID = -8030326394916068947L;
    /**
     * 主键
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
     * 联系类型
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ContactType type;
    /**
     * 类型内容
     */
    @Column(name = "content")
    private String content;


}