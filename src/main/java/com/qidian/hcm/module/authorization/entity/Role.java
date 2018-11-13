package com.qidian.hcm.module.authorization.entity;

import com.qidian.hcm.common.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "role")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
/**
 * 角色表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class Role implements Serializable {

    private static final long serialVersionUID = -4023955031961087440L;
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 角色名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 是否为超级管理员,0否，1是
     */
    @Column(name = "super_admin")
    @Enumerated(value = EnumType.ORDINAL)
    private YesNo superAdmin = YesNo.NO;
    /**
     * 创建时间
     */
    @Column(name = "created_time")
    @CreationTimestamp
    private Date createdTime;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<RoleMenu> roleMenus;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<RolePermission> rolePermissionList;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<EmployeeRole> employeeRoles;

    public Role(String name) {
        this.name = name;
    }

}