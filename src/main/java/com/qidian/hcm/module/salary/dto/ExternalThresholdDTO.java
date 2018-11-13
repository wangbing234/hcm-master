package com.qidian.hcm.module.salary.dto;

import com.qidian.hcm.module.salary.enums.ThresholdType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 免征额【公式配置调用】
 * 引用：${employee.threshold}
 * @author bing.wang
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
public class ExternalThresholdDTO implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    /**
     * 税点
     */
    public Integer point = 0;

    /**
     * 中国公民个人所得税 chinese 外籍人个人所得税   foreign
     */
    public String type = ThresholdType.chinese.name();



}
