package com.cmgmtfs.calcify.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PUBLIC_URLS = {};
    private final BCryptPasswordEncoder encoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Disable CSRF protection (not needed for stateless APIs)
        // and disable CORS (Cross-Origin Resource Sharing).
        http.csrf().disable().cors().disable();

        // Configure session management as stateless because the application does not use sessions.
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Allow all requests to URLs defined in the PUBLIC_URLS array without authentication.
        http.authorizeHttpRequests()
            .requestMatchers(PUBLIC_URLS).permitAll();

        // Restrict DELETE requests to "/user/delete/**" to users with the "DELETE:USER" authority.
        http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.DELETE, "/user/delete/**")
            .hasAnyAuthority("DELETE:USER");

        // Restrict DELETE requests to "/customer/delete/**" to users with the "DELETE:CUSTOMER" authority.
        http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.DELETE, "/customer/delete/**")
            .hasAnyAuthority("DELETE:CUSTOMER");

        // Configure how exceptions are handled:
        // - AccessDeniedHandler: handles requests where the user is authenticated but lacks necessary permissions.
        // - AuthenticationEntryPoint: handles requests where the user is not authenticated.
        // Both handlers are set to `null` here, meaning no custom behavior is defined.
        http.exceptionHandling()
            .accessDeniedHandler(null)
            .authenticationEntryPoint(null);

        // Allow all other requests (not explicitly matched above) to be accessed by anyone without authentication.
        http.authorizeHttpRequests()
            .anyRequest().permitAll();

        // Build and return the SecurityFilterChain object with the above configurations.
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        // create the obj for DoaAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // call the constructor, then give it the encoder and details service
        // details of the user
        authProvider.setUserDetailsService(null);
        authProvider.setPasswordEncoder(encoder);
        // now the provider knows about the user details and the password encoder
        // we will come back to the user details
        // ProviderManager is an implementation of the AuthenticationManager
        return new ProviderManager(authProvider);
    }

    /* Documentations
    https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/authentication/UsernamePasswordAuthenticationFilter.html

    https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationManager.html

    https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationProvider.html

    https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/dao/DaoAuthenticationProvider.html

    https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/dao/AbstractUserDetailsAuthenticationProvider.html
    */
}