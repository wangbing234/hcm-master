package com.qidian.hcm.module.center.config;

import com.qidian.hcm.common.config.HCMConfig;
import com.qidian.hcm.common.constants.SystemConstant;
import com.qidian.hcm.common.exception.BizException;
import com.qidian.hcm.common.interceptor.TenantThreadHelper;
import com.qidian.hcm.common.jwt.JwtUserInfo;
import com.qidian.hcm.common.utils.ResultCode;
import com.qidian.hcm.module.center.entity.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 这个类负责根据租户ID来提供对应的数据源
 *
 * @author bing.wang
 * @version 1.0
 */

@Slf4j
@Service
public class TenantDataSourceProvider {

    // 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant_info表中读出来
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    // 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant_info表中读出来
    private final Map<String, ProcessEngine> processEngineMap = new HashMap<>();

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private DataSource centerDataSource;

    @Autowired
    private HCMConfig hcmConfig;

    /**
     * 静态建立一个数据源，也就是我们的默认数据源，假如我们的访问信息里面没有指定tenantId，就使用默认数据源。
     * 在我这里默认数据源是cloud_config，实际上你可以指向你们的公共信息的库，或者拦截这个操作返回错误。
     */

    @PostConstruct
    void initCenterDataSource() {
        dataSourceMap.put(SystemConstant.DEFAULT_DB, centerDataSource);
    }

    void initDataSource(Iterable<GroupConfig> list) {
        list.forEach(this::addDataSource);
    }

    DataSource getTenantDataSource(String tenantId) {
        if (!dataSourceMap.containsKey(tenantId)) {
            throw new BizException(ResultCode.NOT_FOUND_TENANT);
        }
        return dataSourceMap.get(tenantId);
    }

//    /**
//     * 获取当前租户的数据源
//     *
//     * @return
//     */
//    public DataSource getCurrentTenantDataSource() {
//        JwtUserInfo userToken = TenantThreadHelper.getToken();
//        String tenantId = Objects.nonNull(userToken) ? userToken.getTenantName() : "";
//        if (!dataSourceMap.containsKey(tenantId)) {
//            return dataSourceMap.get(SystemConstant.DEFAULT_DB);
//        }
//        return dataSourceMap.get(tenantId);
//    }

    public ProcessEngine getProcessEngine() {
        JwtUserInfo userToken = TenantThreadHelper.getToken();
        String tenantId = Objects.nonNull(userToken) ? userToken.getTenantName() : "";

        return processEngineMap.computeIfAbsent(tenantId, key ->
                ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                        .setDataSource(getTenantDataSource(tenantId))
                        .setAsyncExecutorActivate(false)
                        .buildProcessEngine()
        );
    }

    public DataSource addDataSource(GroupConfig groupConfig) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create()
                .url(groupConfig.getUrl() + SystemConstant.TENANT_KEY + "_" +
                        groupConfig.getTenantName() + "?" + hcmConfig.getMysqlConnParams())
                .username(groupConfig.getUsername()).password(groupConfig.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName());
        DataSource dataSource = dataSourceBuilder.build();
        dataSourceMap.put(groupConfig.getTenantName(), dataSource);
        return dataSource;
    }

    public void initializeDataBase(String tenantName) {
        //创建数据库
        String dbName = SystemConstant.TENANT_KEY + "_" + tenantName;
        String sb = "create database " + dbName;
        InputStream inputStream = new ByteArrayInputStream(sb.getBytes(StandardCharsets.UTF_8));
        InputStreamResource isr = new InputStreamResource(inputStream);
        ResourceDatabasePopulator populate = new ResourceDatabasePopulator(isr);
        populate.execute(dataSourceMap.get(SystemConstant.DEFAULT_DB));
    }

    public void dropDataBase(Long userId, String tenantName) {
        String dbName = SystemConstant.TENANT_KEY + "_" + tenantName;
        String sb = "USE " + dataSourceProperties.getName();

        sb += ";DELETE FROM company_group WHERE id = (SELECT group_id FROM user WHERE id = " + userId + ")";
        sb += ";DELETE FROM group_config WHERE group_id = (SELECT group_id FROM user WHERE id = " + userId + ")";
        sb += ";DELETE FROM user WHERE group_id = (SELECT group_id FROM (SELECT * FROM user) AS sub_user " +
                "where sub_user.id = " + userId + ")";

        sb += ";DROP DATABASE " + dbName;

        InputStream inputStream = new ByteArrayInputStream(sb.getBytes(StandardCharsets.UTF_8));
        InputStreamResource isr = new InputStreamResource(inputStream);
        ResourceDatabasePopulator populate = new ResourceDatabasePopulator(isr);
        populate.execute(dataSourceMap.get(SystemConstant.DEFAULT_DB));

        dataSourceMap.remove(tenantName);
    }

}
