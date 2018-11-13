package com.qidian.hcm.module.organization.entity;

import com.qidian.hcm.common.enums.YesNo;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "grade")
@Getter
@Setter
@ApiModel(value = "grade")
@EntityListeners(AuditingEntityListener.class)
/**
 * 数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class GradeEntity implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;
    /**
     * 主键（职级代码）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 职级名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 职级简称
     */
    @Column(name = "alias")
    private String alias;

    /**
     * 职级代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 职级级别
     */
    @Column(name = "rank")
    private String rank;

    /**
     * 自定义字段
     */
    @Column(name = "custom_field")
    private String customField;

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
    private YesNo delete;

    /**
     * 生效时间
     */
    @Column(name = "enable_time")
    private Date enableTime;
}