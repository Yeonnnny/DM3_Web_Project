package net.kdigital.ec21.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.handler.CustomerFailureHandler;
import net.kdigital.ec21.handler.CustomerSuccessHandler;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomerFailureHandler failureHandler;
    private final CustomerSuccessHandler successHandler;
 
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 웹 요청 시 접근 권한 설정
        http.authorizeHttpRequests((auth) -> auth.requestMatchers("/",
                "/main/index",
                "/main/index/getCountry",
                "/main/register",
                "/main/registerProc",
                "/main/login",
                "/main/list",
                "/main/downloadImg",
                "/main/productsDetail",
                "/build/**",
                "/data/**",
                "/docs/**",
                "/main/css/**",
                "/main/fonts/**",
                "/main/images/**",
                "/main/js/**",
                "/production/**",
                "/script/**",
                "/src/**",
                "/vendors/**").permitAll()
                .requestMatchers("/manager/**").hasRole("ADMIN")
                .requestMatchers("/inbox/**").hasAnyRole("USER")
                .requestMatchers("/main/**").hasAnyRole("ADMIN", "USER") // 더 일반적인 경로 설정을 이후에 배치
                .anyRequest().authenticated());

        // Customer Login 설정
        http.formLogin((auth) -> auth.loginPage("/main/login")
                .usernameParameter("customerId")
                .passwordParameter("customerPw")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .loginProcessingUrl("/main/loginProc")
                .permitAll());

        // .defaultSuccessUrl("/manager/manager_index")

        // Customer Logout 설정
        http.logout((auth) -> auth.logoutUrl("/main/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));

        // CSRF(Cross-Site Request Forgery) 비활성화
        http.csrf((auth) -> auth.disable()); // 배포시 활성화 해줘야 함

        return http.build();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
