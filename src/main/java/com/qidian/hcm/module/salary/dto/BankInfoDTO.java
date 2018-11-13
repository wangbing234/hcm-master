package com.qidian.hcm.module.salary.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;


/**
 * 员工银行卡信息数据实体类
 *
 * @author rapid-code
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "更新银行卡对象DTO")
public class BankInfoDTO {

    /**
     * 卡号
     */
    @ApiModelProperty(value = "卡号", required = true)
    @Length(min = 12,max = 22)
    private String cardNo;
    /**
     * 开户行名称
     */
    @ApiModelProperty(value = "开户行名称", required = true)
    @Length(min = 4)
    @NotBlank
    private String bankName;
    /**
     * 开户行地址
     */
    @ApiModelProperty(value = "开户行地址", required = true)
    private String bankAddress;


}