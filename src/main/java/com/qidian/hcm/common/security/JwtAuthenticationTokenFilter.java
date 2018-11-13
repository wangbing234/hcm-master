package com.qidian.hcm.common.security;

import com.qidian.hcm.common.constants.SystemConstant;
import com.qidian.hcm.common.interceptor.TenantThreadHelper;
import com.qidian.hcm.common.jwt.*;
import com.qidian.hcm.common.redis.RedisService;
import com.qidian.hcm.module.authorization.dto.PermissionDTO;
import com.qidian.hcm.module.authorization.dto.PermissionMetaDataDTO;
import com.qidian.hcm.module.authorization.dto.RolePermissionDTO;
import com.qidian.hcm.module.authorization.enums.MenuCode;
import com.qidian.hcm.module.authorization.enums.PlatformType;
import com.qidian.hcm.module.employee.entity.Employee;
import com.qidian.hcm.module.employee.service.EmployeePermissionService;
import com.qidian.hcm.module.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_LIST = Arrays.asList(
            "/api/user/phoneCode/*",
            "/api/user/login/**",
            "/api/user/register",
            "/api/user/phone",
            "/api/user/add_not_active_user",
            "/api/user/base_info/*");

    private static final List<String> PRIVATE_LIST = Arrays.asList(
            "/api/user/init",
            "/api/employees/me",
            "/api/employees/*/permission"
    );

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeePermissionService employeePermissionService;
    @Autowired
    private RedisService redisService;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static JwtUserInfo getJwtUser(String token) {
        if (org.springframework.util.StringUtils.isEmpty(token)) {
            return null;
        }
        JwtValidateResult<JwtUserInfo> jwtValidateResult = Jwt.validToken(token);
        if (null != jwtValidateResult && TokenState.VALID.getState().equals(jwtValidateResult.getState().getState())) {
            return jwtValidateResult.getData();
        }
        return null;
    }

    //排除url
    private boolean isMatchUrl(String url, List<String> urlList) {
        for (String pattern : urlList) {
            if (pathMatcher.match(pattern, url)) {
                log.info("exclude url :{} ", url);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        TenantThreadHelper.setToken(null);
        //获取请求地址
        String requestUrl = request.getRequestURI();
        if (!requestUrl.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (isMatchUrl(requestUrl, PUBLIC_LIST)) {
            JwtUserInfo jwtUserInfo = new JwtUserInfo();
            TenantThreadHelper.setToken(jwtUserInfo);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtUserInfo,
                    null, jwtUserInfo.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //设置用户登录状态
            securityContext.setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(SystemConstant.TOKEN);
        JwtUserInfo jwtUserInfo = getJwtUser(token);

        if (Objects.nonNull(jwtUserInfo)) {
            TenantThreadHelper.setToken(jwtUserInfo);
            if (!isMatchUrl(requestUrl, PRIVATE_LIST)) {
                setEmployeeAuthentication(jwtUserInfo);
            }
            //设置权限信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtUserInfo,
                    null, jwtUserInfo.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //设置用户登录状态
            securityContext.setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }

    private void setEmployeeAuthentication(JwtUserInfo jwtUserInfo) {
        Employee employee = employeeService.findByUserId(jwtUserInfo.getId());
        jwtUserInfo.setEmployeeId(employee.getId());
        jwtUserInfo.setSuperAdmin(employee.isSuperAdmin());
        List<JWTGrantedAuthority> authorities = new ArrayList<>();
        if (employee.isSuperAdmin()) {
            authorities.add(new JWTGrantedAuthority("ROLE_ADMIN"));
        }
        RolePermissionDTO rolePermissionDTO =
                (RolePermissionDTO) redisService.get("user:" + jwtUserInfo.getId() + ":permission");
        if (rolePermissionDTO == null) {
            rolePermissionDTO = employeePermissionService.getEmployeePermission(employee.getId());
        }
        if (rolePermissionDTO != null) {
            List<JWTGrantedAuthority> backendAuthorities =
                    getAuthority(PlatformType.backend, rolePermissionDTO.getBackend());
            List<JWTGrantedAuthority> frontendAuthorities =
                    getAuthority(PlatformType.frontend, rolePermissionDTO.getFrontend());
            authorities.addAll(backendAuthorities);
            authorities.addAll(frontendAuthorities);
        }
        jwtUserInfo.setRolePermission(rolePermissionDTO);
        jwtUserInfo.setAuthorities(authorities);
    }

    private List<JWTGrantedAuthority> getAuthority(PlatformType platform,
                                                   List<PermissionMetaDataDTO> permissionMetaDataDTOS) {
        List<JWTGrantedAuthority> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(permissionMetaDataDTOS)) {
            for (PermissionMetaDataDTO backendPermission : permissionMetaDataDTOS) {
                MenuCode menuCode = backendPermission.getCode();
                List<PermissionDTO> permissionDTOS = backendPermission.getPermissions();
                for (PermissionDTO pd : permissionDTOS) {
                    String authority = platform.name() + "|" + menuCode + "|" + pd.getAction().name();
                    authorities.add(new JWTGrantedAuthority(authority));
                }
            }
        }
        return authorities;
    }

}