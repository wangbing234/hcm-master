package com.qidian.hcm.module.organization.entity;

import com.qidian.hcm.common.enums.YesNo;
import com.qidian.hcm.module.organization.enums.OrganizationEnums;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 组织架构实体类
 */
@Entity
@Table(name = "organization")
@Getter
@Setter
@ApiModel(value = "organization")
@EntityListeners(AuditingEntityListener.class)
public class OrganizationEntity implements Serializable {

    private static final long serialVersionUID = 3209056053371675367L;
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 部门代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 部门名称
     */
    @Column(name = "name")
    @NotNull(message = "部门名称不能为空！")
    private String name;

    /**
     * 部门简称
     */
    @Column(name = "alias")
    @NotNull(message = "部门简称不能为空！")
    private String alias;

    /**
     * 上级部门id
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 类型
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OrganizationEnums type;

    /**
     * 生效日期
     */
    @Column(name = "enable_time")
    private Date enableTime;

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

    /**
     * 是否有效
     */
    @Column(name = "enable")
    @Enumerated(EnumType.ORDINAL)
    private YesNo enable;

    /**
     * 是否删除
     */
    @Column(name = "deleted")
    @Enumerated(EnumType.ORDINAL)
    private YesNo deleted;

    /**
     * 部门负责人
     */
    @Column(name = "master")
    private String master;

    /**
     * 自定义字段
     */
    @Column(name = "custom_field")
    private String customField;

    /**
     * 当为公司时，公司法人代表
     */
    @Column(name = "legal_person")
    private String legalPerson;

    /**
     * 当为公司时，表示公司地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 当为公司时，公司注册地址
     */
    @Column(name = "register_address")
    private String registerAddress;

    /**
     * 编制
     */
    @Column(name = "formation")
    private Integer formation;

    private String path;

}