package com.qidian.hcm.module.salary.entity;

import com.qidian.hcm.module.salary.enums.PointRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "social_security_plan")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
/**
 * 社保方案数据实体类
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 * */
public class SocialSecurityPlan implements Serializable {

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
     * 调整日期
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
     * 养老保险（个人）比例
     */
    @Column(name = "pension_personal_ratio")
    private Double pensionPersonalRatio;
    /**
     * 养老保险（公司）比例
     */
    @Column(name = "pension_employer_ratio")
    private Double pensionEmployerRatio;
    /**
     * 医疗保险（个人）比例
     */
    @Column(name = "history_personal_ratio")
    private Double historyPersonalRatio;
    /**
     * 医疗保险（公司）比例
     */
    @Column(name = "history_employer_ratio")
    private Double historyEmployerRatio;
    /**
     * 失业保险（个人）比例
     */
    @Column(name = "outwork_personal_ratio")
    private Double outworkPersonalRatio;
    /**
     * 失业保险（公司）比例
     */
    @Column(name = "outwork_employer_ratio")
    private Double outworkEmployerRatio;
    /**
     * 工伤保险（个人）比例
     */
    @Column(name = "injury_personal_ratio")
    private Double injuryPersonalRatio;
    /**
     * 工伤保险（公司）比例
     */
    @Column(name = "injury_employer_ratio")
    private Double injuryEmployerRatio;
    /**
     * 生育保险（个人）比例
     */
    @Column(name = "birth_personal_ratio")
    private Double birthPersonalRatio;
    /**
     * 生育保险（公司）比例
     */
    @Column(name = "birth_employer_ratio")
    private Double birthEmployerRatio;

}