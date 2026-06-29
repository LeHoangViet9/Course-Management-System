package com.edu.cms.common.security;

import com.edu.cms.common.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Cụm Auth (Đăng nhập, đăng ký, verify công khai)
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/verify", "/api/v1/auth/refresh-token").permitAll()
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/logout").authenticated() // Các tính năng cơ bản cần login

                        // 2. Cụm Quản lý thành viên (Fix khớp với "api/v1/users" không có gạch chéo đầu ở UserController)
                        .requestMatchers("/api/v1/users", "/api/v1/users/**").hasRole("ADMIN")

                        // 3. Cụm Báo cáo thống kê (Chỉ dành riêng cho ADMIN)
                        .requestMatchers("/api/v1/reports/**").hasRole("ADMIN")

                        // 4. Cụm Đăng ký khóa học (Enrollments)
                        .requestMatchers(HttpMethod.GET, "/api/v1/enrollments").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/enrollments").hasRole("STUDENT") // Chỉ học viên mới được đăng ký học
                        .requestMatchers("/api/v1/enrollments/**").hasAnyRole("STUDENT", "ADMIN")

                        // 5. Cụm Đánh giá/Bình luận (Reviews)
                        // GET list review theo course công khai, các thao tác POST/PUT/DELETE cần auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/*/course").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reviews/*/course").hasRole("STUDENT")
                        .requestMatchers("/api/v1/reviews/**").authenticated()

                        // 6. Cụm Bài học (Lessons)
                        // Cho phép xem preview bài học khi đã đăng nhập
                        .requestMatchers(HttpMethod.GET, "/api/v1/lessons/*/preview").authenticated()
                        // Các tác vụ quản lý bài học (Thêm, sửa, xóa, publish) thuộc về Giáo viên hoặc Admin
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/*/lessons").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/lessons/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lessons/**").hasAnyRole("TEACHER", "ADMIN")

                        // 7. Cụm Khóa học (Courses)
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses", "/api/v1/courses/**").permitAll() // Khách vãng lai xem danh sách/chi tiết
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/courses/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/courses/**").hasAnyRole("TEACHER", "ADMIN")

                        // 8. Cụm Thông báo (Notifications)
                        .requestMatchers("/api/v1/notifications/**").authenticated()

                        // Tất cả các request phát sinh khác bắt buộc phải qua login
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}