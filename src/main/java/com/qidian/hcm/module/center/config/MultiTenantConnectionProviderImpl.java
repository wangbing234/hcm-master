package com.qidian.hcm.module.center.config;

import com.qidian.hcm.common.constants.SystemConstant;
import com.qidian.hcm.module.center.repository.GroupConfigRepository;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 这个类是Hibernate框架拦截sql语句并在执行sql语句之前更换数据源提供的类
 *
 * @author lanyuanxiaoyao
 * @version 1.0
 */
@Component
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private static final long serialVersionUID = 1;

    @Autowired
    private transient TenantDataSourceProvider tenantDataSourceProvider;

    @Autowired
    private transient ApplicationContext context;

    private boolean init = false;

    // 在没有提供tenantId的情况下返回默认数据源
    @Override
    protected DataSource selectAnyDataSource() {
        return tenantDataSourceProvider.getTenantDataSource(SystemConstant.DEFAULT_DB);
    }

    // 提供了tenantId的话就根据ID来返回数据源
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        if (!init) {
            init = true;
            GroupConfigRepository groupConfigRepository = context.getBean(GroupConfigRepository.class);
            tenantDataSourceProvider.initDataSource(groupConfigRepository.findAll());
        }
        return tenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
    }
}
