package com.qidian.hcm.module.authorization.entity;

import com.qidian.hcm.module.authorization.enums.ActionType;
import com.qidian.hcm.module.authorization.enums.EffectType;
import com.qidian.hcm.module.authorization.enums.MenuCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "role_permission")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
/**
 * 角色权限表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class RolePermission implements Serializable {

    private static final long serialVersionUID = -4024711044554886617L;
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
     * 菜单code
     */
    @Column(name = "menu_code")
    @Enumerated(value = EnumType.STRING)
    private MenuCode menuCode;

    /**
     * 资源
     */
    @Column(name = "resource")
    private String resource;
    /**
     * 操作类型,GET，DELETE，PUT，POST,EDIT
     */
    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    private ActionType action;
    /**
     * 权限条件
     */
    @Column(name = "conditions")
    private String conditions;
    /**
     * 访问状态，allow允许,deny拒绝
     */
    @Column(name = "effect")
    @Enumerated(value = EnumType.STRING)
    private EffectType effect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    public RolePermission(Long roleId, MenuCode menuCode, String resource, ActionType action, String conditions) {
        this.roleId = roleId;
        this.menuCode = menuCode;
        this.resource = resource;
        this.action = action;
        this.conditions = conditions;
        this.effect = EffectType.allow;
    }
}