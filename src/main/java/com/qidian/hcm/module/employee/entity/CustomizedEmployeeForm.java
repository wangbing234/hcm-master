package com.qidian.hcm.module.employee.entity;

import com.qidian.hcm.module.employee.enums.FormType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "customized_employee_form")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 自定义员工表单表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class CustomizedEmployeeForm implements Serializable {

    private static final long serialVersionUID = 4887221432178766843L;
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;
    /**
     * 模块名称
     */
    @Column(name = "title")
    private String title;
    /**
     * 表单类型
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private FormType type;
    /**
     * 是否必填，0否，1是
     */
    @Column(name = "required")
    private Boolean required;
    /**
     * 是否支持多个，0否，1是
     */
    @Column(name = "multi_record")
    private Boolean multiRecord;
    /**
     * 员工入职是否启用：0否，1是
     */
    @Column(name = "on_board")
    private Boolean onBoard;
    /**
     * 开启状态，0关闭，1开启
     */
    @Column(name = "switchable")
    private Boolean switchable;
    /**
     * 开关是否禁用，0否，1是
     */
    @Column(name = "enable")
    private Boolean enable;
    /**
     * 排序
     */
    @Column(name = "idx")
    private Integer idx;
    /**
     * 创建人：0系统，1用户
     */
    @Column(name = "created_by")
    private Long createdBy;


}