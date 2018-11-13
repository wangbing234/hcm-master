package com.qidian.hcm.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private static final List<String> AUTH_LIST = Arrays.asList(
            "/swagger-resources/**",
            "/swagger-ui.html**",
            "/webjars/**",
            "favicon.ico");


    @Override
    @SuppressWarnings("PMD")
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/login") // 登录请求路径
//                .successHandler(furyAuthenticationSuccessHandler) // 验证成功处理器
//                .failureHandler(furyAuthenticationFailureHandler) // 验证失败处理器
                .and().antMatcher("/api/**").authorizeRequests().anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(swaggerAuthenticationEntryPoint(),
                        new CustomRequestMatcher(AUTH_LIST))
                .and()
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()

//                .antMatchers("/api/position/**").hasRole("ADMIN")
                //以"/admin"开始的URL，并需拥有 "ROLE_ADMIN" 角色权限和 "ROLE_DBA" 角色,这里不需要写"ROLE_"前缀；
//                .antMatchers("/dba/**").access("hasRole('ADMIN') and hasRole('DBA')")
                //前面没有匹配上的请求，全部需要认证；
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl("/api/logout") //指定登出的地址，默认是"/logout"
                .invalidateHttpSession(true)  //定义登出时是否invalidate HttpSession，默认为true
                .deleteCookies("usernameCookie", "urlCookie"); //在登出同时清除cookies

        // 禁用缓存
        http.headers().cacheControl();
        // 添加JWT filter
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
//        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("USER");
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public BasicAuthenticationEntryPoint swaggerAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("Swagger Realm");
        return entryPoint;
    }

    private static class CustomRequestMatcher implements RequestMatcher {
        private final List<AntPathRequestMatcher> matchers;

        private CustomRequestMatcher(List<String> matchers) {
            this.matchers = matchers.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            return matchers.stream().anyMatch(a -> a.matches(request));
        }
    }


}
