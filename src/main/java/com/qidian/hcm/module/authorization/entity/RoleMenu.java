package com.qidian.hcm.module.authorization.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "role_menu")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
/**
 * 角色菜单表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = -2035165381509339085L;
    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 角色ID
     */
    @Column(name = "role_id")
    private Long roleId;
    /**
     * 菜单ID
     */
    @Column(name = "menu_id")
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    public RoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}