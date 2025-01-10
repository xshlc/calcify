package com.cmgmtfs.calcify.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    private static final String[] PUBLIC_URLS = {};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf()
//                .disable()
//                .cors()
//                .disable();
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.authorizeRequests().antMatchers(PUBLIC_URLS)
//                .permitAll();
//        http.authorizeRequests().antMatchers(HttpMethd.DELETE, "/user/delete/**").hasAnyAuthority("DELETE:USER");
//        http.authorizeRequests().antMatchers(HttpMethd.DELETE, "/user/delete/**").hasAnyAuthority("DELETE:USER");
        return http.build();
    }
}
