package com.qidian.hcm.module.center.config;

import com.qidian.hcm.common.constants.SystemConstant;
import com.qidian.hcm.module.center.entity.GroupConfig;
import com.qidian.hcm.module.center.repository.GroupConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FlywayConfigurer implements ApplicationRunner {

    @Autowired
    private GroupConfigRepository groupConfigRepository;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    private static final String CENTER_SCRIPT_LOCATION = "classpath:db/migration/center";
    private static final String TENANT_SCRIPT_LOCATION = "classpath:db/migration/tenant";

    @Override
    public void run(ApplicationArguments args) {
        migrateForCenterDB();

        List<GroupConfig> groupConfigs = groupConfigRepository.findAll();
        Set<String> tenantNames = groupConfigs.stream().map(GroupConfig::getTenantName).collect(Collectors.toSet());

        for (String tenantName : tenantNames) {
            String dbName = SystemConstant.TENANT_KEY + "_" + tenantName;
            migrateForTenantDB(dbName);
        }
    }

    private void migrateForCenterDB() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);

        String url = dataSourceProperties.getUrl() + dataSourceProperties.getName();
        flyway.setLocations(CENTER_SCRIPT_LOCATION);
        flyway.setDataSource(url, dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
        flyway.migrate();
    }

    public void migrateForTenantDB(String dbName) {
        Flyway flyway = new Flyway();
        flyway.setPlaceholderPrefix("##(");
        flyway.setPlaceholderSuffix(")");
        String url = dataSourceProperties.getUrl() + dbName;
        flyway.setLocations(TENANT_SCRIPT_LOCATION);
        flyway.setDataSource(url, dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
        flyway.migrate();
    }

}
