package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "salary_item")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class SalaryItem implements Serializable {

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
     * 编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 税前加项
     * 税前减项
     * 税后加项
     * 税后减项
     * 不直接参与薪资计算项
     * 人力成本项
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SalaryType type;
    /**
     * 小数点保留位数
     */
    @Range(min = 0, max = 4)
    @Column(name = "point_scale")
    private Integer pointScale;
    /**
     * 小数点进位规则
     */
    @Column(name = "point_rule")
    @Enumerated(EnumType.STRING)
    private PointRule pointRule;
    /**
     * 公式
     */
    @Column(name = "formula")
    private String formula;
    /**
     * 是否显示在个人明细
     */
    @Column(name = "display")
    private Boolean display;

    /**
     * 是否可以删除
     */
    @Column(name = "deletable")
    private Boolean deletable;

    /**
     * 是否可以编辑
     */
    @Column(name = "editable")
    private Boolean editable;

    /**
     * 是否显示在列表中
     */
    @Column(name = "in_list")
    private Boolean inList;

    /**
     * 是否显示在下拉的编辑项中
     */
    @Column(name = "in_option")
    private Boolean inOption;

    /**
     * 是否是输入的（目前是考勤字段才输入）
     */
    @Column(name = "inflow")
    private Boolean inflow;
}