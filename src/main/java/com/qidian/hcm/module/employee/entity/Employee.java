package com.qidian.hcm.module.employee.entity;

import com.qidian.hcm.module.employee.enums.EmployeeStatus;
import com.qidian.hcm.module.employee.enums.EmployeeType;
import com.qidian.hcm.module.employee.enums.Gender;
import com.qidian.hcm.module.salary.entity.EmployeeFinancial;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "employee")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 员工表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class Employee implements Serializable {

    private static final long serialVersionUID = 895958804587817183L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 员工编号，员工唯一标识，不能重复
     */
    @Column(name = "employee_no")
    private String employeeNo;
    /**
     * 姓名
     */
    @Column(name = "name")
    private String name;
    /**
     * 头像
     */
    @Column(name = "avatar_file_id")
    private Long avatarFileId;
    /**
     * 手机号，将作为员工登录账号
     */
    @Column(name = "mobile")
    private String mobile;
    /**
     * 性别
     */
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    /**
     * 生日
     */
    @Column(name = "birthday")
    private Date birthday;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    private Date hireDate;
    /**
     * 开始工作日期
     */
    @Column(name = "work_date")
    private Date workDate;
    /**
     * 员工类型
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EmployeeType type;
    /**
     * 员工状态：0试用，1正式，2离职
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    /**
     * 转正日期
     */
    @Column(name = "qualify_date")
    private Date qualifyDate;

    @Column(name = "customized_forms")
    private String customizedForms;

    /**
     * 转正备注
     */
    @Column(name = " qualify_remark")
    private String qualifyRemark;
    /**
     * 转正信在oss上的fileid
     */
    @Column(name = "qualify_letter_file_id")
    private Long qualifyLetterFileId;
    /**
     * 转正信的原始名称（冗余字段）
     */
    @Column(name = "qualify_letter_origin_name")
    private String qualifyLetterOriginName;
    /**
     * 离职日期
     */
    @Column(name = "resignation_date")
    private Date resignationDate;
    /**
     * 离职原因
     */
    @Column(name = "resignation_reason")
    private String resignationReason;
    /**
     * 离职信在oss上的fileid
     */
    @Column(name = "resignation_letter_file_id")
    private Long resignationLetterFileId;
    /**
     * 离职信的原始名称（冗余字段）
     */
    @Column(name = "resignation_letter_origin_name")
    private String resignationLetterOriginName;
    /**
     * 离职交接人id
     */
    @Column(name = "resignation_handover_man_id")
    private Long resignationHandoverManId;
    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "super_admin")
    private boolean superAdmin;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private List<EmployeePosition> employeePositions;

    @OneToOne(mappedBy = "employee")
    private EmployeeFinancial employeeFinancial;


}