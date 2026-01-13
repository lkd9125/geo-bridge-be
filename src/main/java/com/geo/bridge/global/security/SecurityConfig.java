package com.geo.bridge.global.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.geo.bridge.domain.user.repository.UserRepository;
import com.geo.bridge.global.security.authentication.AuthenticationWebLoginConverter;
import com.geo.bridge.global.security.authentication.AuthenticationWebLoginManager;
import com.geo.bridge.global.security.authentication.AuthenticationWebLoginSuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    /**
     * Security Filter 전체 설정
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.mode(Mode.SAMEORIGIN)))
            .formLogin(formLogin -> formLogin.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .addFilterAt(this.authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange(auth -> 
                auth.pathMatchers("/**").permitAll()
            )
            .build();
    }

    /**
     * PASSWORD Encoder
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token", "Accept"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Login Authentication Filter
     * @return
     */
    @Bean
    public AuthenticationWebFilter authenticationWebFilter(){

        // [CUSTOM] Security ReactiveAuthenticationManager(MVC의 Authentication Provider와 같음) 
        AuthenticationWebLoginManager authenticationWebLoginManager = new AuthenticationWebLoginManager(this.passwordEncoder(), userRepository);
        // [CUSTOM] Login RQ convert
        AuthenticationWebLoginConverter authenticationWebLoginConverter = new AuthenticationWebLoginConverter();
        // [CUST] Loing Success Handler
        AuthenticationWebLoginSuccessHandler authenticationWebLoginSuccessHandler = new AuthenticationWebLoginSuccessHandler(tokenProvider);

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationWebLoginManager);
        authenticationWebFilter.setServerAuthenticationConverter(authenticationWebLoginConverter);
        authenticationWebFilter.setAuthenticationSuccessHandler(authenticationWebLoginSuccessHandler);

        return authenticationWebFilter;
    }

}