package com.qidian.hcm.module.salary.dto;

import com.qidian.hcm.module.salary.enums.PointRule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 社保方案【公式配置调用】
 * 引用：${employee.socialSecurity}
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class ExternalSocialSecurityPlanDTO implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    /**
     * 入离职临界点
     */
    public Integer limitPoint = 1;
    /**
     * 调整日期
     */
    public Date effectDate;
    /**
     * 小数点保留位数
     */
    public Integer pointScale = 1;
    /**
     * 小数点进位规则
     */
    public String pointRule = PointRule.round.name();
    /**
     * 养老保险（个人）比例
     */
    public Double pensionPersonalRatio = 0d;
    /**
     * 养老保险（公司）比例
     */
    public Double pensionEmployerRatio = 0d;
    /**
     * 医疗保险（个人）比例
     */
    public Double historyPersonalRatio = 0d;
    /**
     * 医疗保险（公司）比例
     */
    public Double historyEmployerRatio = 0d;
    /**
     * 失业保险（个人）比例
     */
    public Double outworkPersonalRatio = 0d;
    /**
     * 失业保险（公司）比例
     */
    public Double outworkEmployerRatio = 0d;
    /**
     * 工伤保险（个人）比例
     */
    public Double injuryPersonalRatio = 0d;
    /**
     * 工伤保险（公司）比例
     */
    public Double injuryEmployerRatio = 0d;
    /**
     * 生育保险（个人）比例
     */
    public Double birthPersonalRatio = 0d;
    /**
     * 生育保险（公司）比例
     */
    public Double birthEmployerRatio = 0d;

}
