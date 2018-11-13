package com.qidian.hcm.module.salary.dto;

import com.qidian.hcm.module.salary.enums.PointRule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 公积金方案【公式配置调用】
 * 引用：${employee.housingFund}
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class ExternalHousingFundPlanDTO implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    /**
     * 入离职临界点
     */
    public Integer limitPoint = 1;
    /**
     * 生效日期
     */
    public Date effectDate;
    /**
     * 小数点保留位数
     */
    public Integer pointScale = 1;
    /**
     * 小数点进位规则
     * round：四舍五入
     * floor：直接保留
     */
    public String pointRule = PointRule.round.name();

    /**
     * 公积金（公司）比例
     */
    public Double fundEmployerRatio = 0d;
    /**
     * 公积金（个人）比例
     */
    public Double fundPersonalRatio = 0d;
    /**
     * 补充公积金（公司）比例
     */
    public Double fundAddingEmployerRatio = 0d;
    /**
     * 补充公积金（个人）比例
     */
    public Double fundAddingPersonalRatio = 0d;

}
