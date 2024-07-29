package com.example.qnacomunity.config;

import com.example.qnacomunity.security.JwtFilter;
import com.example.qnacomunity.security.JwtUtil;
import com.example.qnacomunity.security.MemberDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final MemberDetailService memberDetailService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http //page 부분은 프론트 에서 구현
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(AntPathRequestMatcher.antMatcher("/page/home")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/page/sign-in")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/page/sign-up")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/page/logout")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/member/sign-in")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/member/sign-up")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/member/get-role")).permitAll()
            .anyRequest().authenticated()
        );

    http //JWT 토큰 확인 필터를 UsernamePasswordAuthenticationFilter 보다 앞에 위치
        .addFilterBefore(new JwtFilter(jwtUtil, memberDetailService),
            UsernamePasswordAuthenticationFilter.class);

    http //로그아웃
        .logout((auth) -> auth
            .logoutUrl("/logout-security")
            .logoutSuccessUrl("/page/home")
        );

    http //폼 로그인, csrf disable
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable);

    http //oauth 구글 로그인
        .oauth2Login((auth) -> auth.loginPage("/page/sign-in")
            .defaultSuccessUrl("/page/home")
            .failureUrl("/page/sign-in")
            .permitAll());

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}