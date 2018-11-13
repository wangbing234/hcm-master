package com.qidian.hcm.module.employee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author lyn
 * @date 2018/8/14 17 04
 */


@Getter
@Setter
@ApiModel(value = "员工历史信息")
@AllArgsConstructor
public class EmployeeHistoryDTO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", name = "id", required = true)
    private Long id;
    /**
     *
     */
    @ApiModelProperty(name = "content", required = true)
    private String content;

    /**
     *
     */
    @ApiModelProperty(name = "remark", required = true)
    private String remark;

    /**
     *
     */
    @ApiModelProperty(name = "date", required = true)
    private Date date;
}
