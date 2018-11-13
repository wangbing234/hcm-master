package com.qidian.hcm.module.authorization.entity;

import com.qidian.hcm.module.authorization.enums.MenuCode;
import com.qidian.hcm.module.authorization.enums.MenuType;
import com.qidian.hcm.module.authorization.enums.PlatformType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "menu")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 菜单表数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class Menu implements Serializable {

    private static final long serialVersionUID = 3135375149693232480L;
    /**
     * 主键ID(自增序列)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 菜单名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 菜单编码
     */
    @Column(name = "code")
    @Enumerated(value = EnumType.STRING)
    private MenuCode code;
    /**
     * 父菜单ID，一级菜单为0
     */
    @Column(name = "parent_id")
    private Long parentId;
    /**
     * 排序
     */
    @Column(name = "idx")
    private Integer idx;

    /**
     * 菜单类型，catalogue(目录)，menu(菜单),button(按钮)
     */
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private MenuType type;

    /**
     * 平台类型,backend(管理后台权限),frontend(自助服务权限)
     */
    @Column(name = "platform")
    @Enumerated(value = EnumType.STRING)
    private PlatformType platform;
}