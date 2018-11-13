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
@Table(name = "position")
@Getter
@Setter
@ApiModel(value = "position")
@EntityListeners(AuditingEntityListener.class)
/**
 * 数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class PositionEntity implements Serializable {

    private static final long serialVersionUID = -8361627688595663478L;
    /**
     * 主键（岗位ID）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 岗位名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 岗位代码
     */
    @Column(name = "code")
    private String code;
    /**
     * 岗位简称
     */
    @Column(name = "alias")
    private String alias;
    /**
     * 所属部门ID
     */
    @Column(name = "department_id")
    private Long departmentId;
    /**
     * 上级岗位ID
     */
    @Column(name = "parent_position_id")
    private Long parentPositionId;
    /**
     * 职级ID
     */
    @Column(name = "grade_id")
    private Long gradeId;
    /**
     * 是否有效
     */
    @Column(name = "enable")
    @Enumerated(EnumType.ORDINAL)
    private YesNo enable;
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private Date createTime;
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", insertable = false)
    private Date updateTime;
    /**
     * 生效日期
     */
    @Column(name = "enable_time")
    private Date enableTime;
    /**
     * 自定义字段
     */
    @Column(name = "custom_field")
    private String customField;
    /**
     * 是否被删除
     */
    @Column(name = "deleted")
    @Enumerated(EnumType.ORDINAL)
    private YesNo deleted;
}