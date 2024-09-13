package account.configuration;

import account.domain.Role;
import account.exception.CustomAccessDeniedHandler;
import account.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    public static final String SIGN_UP_API = "/api/auth/signup";
    public static final String AUDIT_API = "/api/security/events/";
    public static final String CHANGE_PASSWORD_API = "/api/auth/changepass";
    public static final String PAYMENT_API = "/api/empl/payment";
    public static final String PAYMENTS_API = "/api/acct/payments";
    public static final String USER_API = "/api/admin/user";

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .csrf(AbstractHttpConfigurer::disable) // For Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth  // manage access
                                .requestMatchers(HttpMethod.POST, SIGN_UP_API).permitAll()
                                .requestMatchers(HttpMethod.GET, AUDIT_API).hasAuthority(Role.ROLE_AUDITOR.toString())
                                .requestMatchers(HttpMethod.POST, CHANGE_PASSWORD_API)
                                .hasAnyAuthority(Role.ROLE_USER.toString(), Role.ROLE_ACCOUNTANT.toString(), Role.ROLE_ADMINISTRATOR.toString())
                                .requestMatchers(HttpMethod.GET, PAYMENT_API)
                                .hasAnyAuthority(Role.ROLE_USER.toString(), Role.ROLE_ACCOUNTANT.toString())
                                .requestMatchers(HttpMethod.POST, PAYMENTS_API).hasAuthority(Role.ROLE_ACCOUNTANT.toString())
                                .requestMatchers(HttpMethod.PUT, PAYMENTS_API).hasAnyAuthority(Role.ROLE_ACCOUNTANT.toString())
                                .requestMatchers(USER_API + "/**").hasAuthority(Role.ROLE_ADMINISTRATOR.toString())
                                .anyRequest().permitAll()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

}
