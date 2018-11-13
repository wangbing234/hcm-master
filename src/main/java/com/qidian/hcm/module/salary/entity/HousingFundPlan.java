package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.salary.enums.PointRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "housing_fund_plan")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 公积金方案数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class HousingFundPlan implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 方案名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 入离职临界点
     */
    @Column(name = "limit_point")
    private Integer limitPoint;
    /**
     * 生效日期
     */
    @Column(name = "effect_date")
    private Date effectDate;
    /**
     * 小数点保留位数
     */
    @Column(name = "point_scale")
    private Integer pointScale;
    /**
     * 小数点进位规则
     */
    @Column(name = "point_rule")
    @Enumerated(EnumType.STRING)
    private PointRule pointRule;
    /**
     * 社保缴纳上限
     */
    @Column(name = "limit_up")
    private Long limitUp;
    /**
     * 社保缴纳下限
     */
    @Column(name = "limit_down")
    private Long limitDown;
    /**
     * 公积金（公司）比例
     */
    @Column(name = "fund_employer_ratio")
    private Double fundEmployerRatio;
    /**
     * 公积金（个人）比例
     */
    @Column(name = "fund_personal_ratio")
    private Double fundPersonalRatio;
    /**
     * 补充公积金（公司）比例
     */
    @Column(name = "fund_adding_employer_ratio")
    private Double fundAddingEmployerRatio;
    /**
     * 补充公积金（个人）比例
     */
    @Column(name = "fund_adding_personal_ratio")
    private Double fundAddingPersonalRatio;

}