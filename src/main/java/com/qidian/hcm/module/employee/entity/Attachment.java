package com.qidian.hcm.module.employee.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 附件表数据实体类
 */
@Entity
@Table(name = "attachment")
@Getter
@Setter
@ApiModel(value = "attachment")
public class Attachment implements Serializable {
    private static final long serialVersionUID = 3945880025208762989L;
    /**
     * 自增长ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 原始文件名称
     */
    @Column(name = "origin_name")
    private String originName;

    /**
     * 通过全局唯一ID生成器生成的文件ID
     */
    @Column(name = "file_id")
    private Long fileId;

    /**
     * 附件在阿里云oss服务器中的文件名称
     */
    @Column(name = "oss_filename")
    private String fileNameOnOss;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_time", nullable = true, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

}
