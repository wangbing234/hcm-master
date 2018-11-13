package com.qidian.hcm.module.organization.response;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qidian.hcm.common.enums.YesNo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ApiModel
@Entity
public class GradeResponse {
    /**
     * 主键（职级代码）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键（职级代码）", name = "id", required = true)
    private Long id;
    /**
     * 职级名称
     */
    @ApiModelProperty(value = "职级名称", name = "name", required = true)
    private String name;
    /**
     * 职级简称
     */
    @ApiModelProperty(value = "职级简称", name = "alias", required = true)
    private String alias;

    /**
     * 职级代码
     */
    @ApiModelProperty(value = "职级代码", name = "code")
    private String code;

    /**
     * 职级级别
     */
    @ApiModelProperty(value = "职级级别", name = "rank", required = true)
    private String rank;

    /**
     * 自定义字段
     */
    private JSONObject customField;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", name = "create_time", required = true)
    private Date createTime;
    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", name = "update_time", required = true)
    private Date updateTime;
    /**
     * 是否有效
     */
    @ApiModelProperty(value = "是否有效", name = "enable", required = true)
    private YesNo enable;

    /**
     * 是否删除
     */
    private YesNo delete;

    /**
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "", name = "enable_time", required = true)
    private Date enableTime;
}
