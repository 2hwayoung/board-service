package com.example.restapi.global.security;

import com.example.restapi.global.app.AppConfig;
import com.example.restapi.global.dto.RsData;
import com.example.restapi.standard.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/h2-console/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/*/posts", "/api/*/posts/{id:\\d+}", "/api/*/posts/{postId:\\d+}/comments")
                                .permitAll()
                                .requestMatchers("/api/*/members/join", "/api/*/members/login", "/api/*/members/logout")
                                .permitAll()
                                .requestMatchers("/api/*/posts/statistics")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/*/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                        )))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(
                                        (request, response, exception) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    Utils.Json.toString(
                                                            new RsData<>("401-1", "잘못된 인증키입니다.")
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    Utils.Json.toString(
                                                            new RsData<>("403-1", "접근 권한이 없습니다.")
                                                    )
                                            );
                                        }
                                )
                )

        ;
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 오리진 설정
        configuration.setAllowedOrigins(List.of(AppConfig.getSiteFrontUrl()));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));
        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
