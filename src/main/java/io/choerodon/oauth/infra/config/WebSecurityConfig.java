package io.choerodon.oauth.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import io.choerodon.oauth.infra.common.util.*;

/**
 * @author wuguokai
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationDetailSource detailSource;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private SingleLoginSessionInformationExpiredStrategy sessionInformationExpiredStrategy;
    @Autowired
    private OauthProperties properties;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/public/**", "/password/**", "/static/**",
                        "/forgetPassword/**", "/wechat/**")
                // .antMatchers("/oauth/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .authenticationDetailsSource(detailSource)
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .logout().deleteCookies("access_token").invalidateHttpSession(true)
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .and()
                .csrf()
                .disable();
        if (properties.isEnabledSingleLogin()) {
            http.sessionManagement().maximumSessions(1).expiredSessionStrategy(sessionInformationExpiredStrategy);
        }
    }
}
