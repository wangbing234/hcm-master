package com.qidian.hcm.module.salary.dto;

import com.qidian.hcm.module.salary.enums.PointRule;
import com.qidian.hcm.module.salary.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FormulaDTO implements Serializable {

    private static final long serialVersionUID = 5297241045009352146L;

    private Long id;

    private String code;

    private SalaryType type;

    private Integer pointScale;

    private PointRule pointRule;

    private String formula;

    //所有子公式
    private List<String> children;

    //所有父亲公式
    private List<String>  parents;

}
