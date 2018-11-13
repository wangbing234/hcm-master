package com.qidian.hcm.module.salary.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 薪资配置表数据实体类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "salary_setting")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class SalarySetting implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 键
     */
    @Column(name = "setting_key")
    private String key;
    /**
     * 值
     */
    @Column(name = "setting_value")
    private String value;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    public SalarySetting(String name, String key, String value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }
}