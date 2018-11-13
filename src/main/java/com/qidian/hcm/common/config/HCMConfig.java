package com.qidian.hcm.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置类
 */
@Configuration
@Getter
public class HCMConfig {

    /**
     * 租户数据源连接
     */
    @Value("${hcm.config.tenant-url:http://127.0.0.1/}")
    public String tenantUrl;

    /**
     * 阿里云oss endpoint
     */
    @Value("${hcm.aliyun-oss.endpoint}")
    public String aliyunOssEndpoint;

    /**
     * 阿里云oss endpoint url
     */
    @Value("${hcm.aliyun-oss.endpoint-url}")
    public String aliyunOssEndpointUrl;

    /**
     * 阿里云oss accessKeyId
     */
    @Value("${hcm.aliyun-oss.access-key-id}")
    public String aliyunOssAccessKeyId;

    /**
     * 阿里云oss accessKeySecret
     */
    @Value("${hcm.aliyun-oss.access-key-secret}")
    public String aliyunOssAccessKeySecret;

    /**
     * 阿里云oss 存储桶名称
     */
    @Value("${hcm.aliyun-oss.bucket-name}")
    public String aliyunOssBucketName;

    /**
     * 阿里云oss 附件大小限制，单位: b
     */
    @Value("${hcm.aliyun-oss.max-attachment-size}")
    public String aliyunOssMaxAttSize;


    /**
     * 阿里云oss 临时访问链接有效期，单位: ms
     */
    @Value("${hcm.aliyun-oss.temp-url-expire}")
    public Long aliyunOssTempUrlExpire;

    /**
     * 雪花算法数据中心ID
     */
    @Value("${hcm.snow-flake.datacenter-id}")
    public Long snowFlakeDatacenterId;

    /**
     * 雪花算法机器ID
     */
    @Value("${hcm.snow-flake.machine-id}")
    public Long snowFlakeMachineId;

    /**
     * mysql连接参数
     */
    @Value("${hcm.mysql-conn-params}")
    public String mysqlConnParams;

    /**
     * 用户登陆验证码有效期(ms) 15分钟
     */
    @Value("${hcm.login-verification-code-effective-time}")
    public Long loginVerificationCodeEffectiveTime;

    /**
     * 短信API产品名称(短信产品名固定，无需修改)
     */
    @Value("${hcm.aliyun-sms.sms-product}")
    public String product;

    /**
     * 短信API产品域名(接口地址固定，无需修改)
     */
    @Value("${hcm.aliyun-sms.sms-domain}")
    public String domain;

    /**
     * accessKeyId
     */
    @Value("${hcm.aliyun-sms.accesskey-id}")
    public String accessKeyId;

    /**
     * accessKeySecret
     */
    @Value("${hcm.aliyun-sms.accesskey-secret}")
    public String accessKeySecret;

    /**
     * 短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
     */
    @Value("${hcm.aliyun-sms.template-code}")
    public String templateCode;

}
