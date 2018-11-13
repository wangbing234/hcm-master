package com.qidian.hcm.module.center.config;

import com.qidian.hcm.common.constants.SystemConstant;
import com.qidian.hcm.common.interceptor.TenantThreadHelper;
import com.qidian.hcm.common.jwt.JwtUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 这个类是由Hibernate提供的用于识别tenantId的类，当每次执行sql语句被拦截就会调用这个类中的方法来获取tenantId
 *
 * @author bing.wang
 * @version 1.0
 */
@Component
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    /**
     * 获取tenantId的逻辑在这个方法里面写
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        JwtUserInfo user = TenantThreadHelper.getToken();
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getTenantName())) {
            return user.getTenantName();
        }
        return SystemConstant.DEFAULT_DB;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
