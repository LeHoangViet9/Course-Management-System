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

                        // 1. Cụm AUTH & PUBLIC (STT: 1)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()

                        // Cụm AUTH chung - Yêu cầu Đăng nhập (STT: 2, 3, 30)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/verify", "/api/v1/auth/refresh-token", "/api/v1/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()

                        // 2. Cụm USERS (STT: 4, 5, 6, 7, 8, 9, 31)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users", "api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}", "api/v1/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}/role", "api/v1/users/{id}/status", "api/v1/users/{id}/role", "api/v1/users/{id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}", "api/v1/users/{id}").hasRole("ADMIN")
                        // API cập nhật cá nhân / đổi mật khẩu (STT: 26, 27) -> OWNER hoặc ADMIN kiểm tra tại tầng Controller
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}", "/api/v1/users/{id}/password", "api/v1/users/{id}", "api/v1/users/{id}/password").authenticated()

                        // 3. Cụm COURSES (STT: 10, 11, 12, 13, 14, 15, 28, 29, 32)
                        // Lấy danh sách, chi tiết khóa học, hoặc danh sách bài học thuộc khóa học -> Đều cần AUTH
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses", "/api/v1/courses/**").authenticated()
                        // Tạo, Sửa, Xóa khóa học -> Chỉ ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/courses/{id}", "/api/v1/courses/{id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/courses/{id}").hasRole("ADMIN")
                        // Tạo bài học trong Course (STT 18: /api/v1/courses/{courseId}/lessons) -> TEACHER hoặc ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/{courseId}/lessons").hasAnyRole("TEACHER", "ADMIN")

                        // 4. Cụm LESSONS (STT: 16, 17, 19, 20, 21, 44)
                        // Xem bài học, xem nội dung preview bài học -> Cần AUTH
                        .requestMatchers(HttpMethod.GET, "/api/v1/lessons/{id}", "/api/v1/lessons/{id}/content-preview").authenticated()
                        // Sửa, xóa, thay đổi trạng thái hiển thị bài học -> TEACHER hoặc ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/lessons/{id}", "/api/v1/lessons/{id}/publish").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lessons/{id}").hasAnyRole("TEACHER", "ADMIN")

                        // 5. Cụm ENROLLMENTS (STT: 22, 23, 24, 25) -> Chỉ dành riêng cho STUDENT đăng ký và học
                        .requestMatchers("/api/v1/enrollments", "/api/v1/enrollments/**").hasRole("STUDENT")

                        // 6. Cụm NOTIFICATIONS (STT: 33, 34, 35, 36)
                        .requestMatchers(HttpMethod.GET, "/api/v1/notifications").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notifications/{id}/read").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/notifications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/notifications/{id}").hasRole("ADMIN")

                        // 7. Cụm REPORTS (STT: 37, 38, 39) -> Chỉ duy nhất ADMIN được xem báo cáo thống kê
                        .requestMatchers("/api/v1/reports/**").hasRole("ADMIN")

                        // 8. Cụm REVIEWS (STT: 40, 41, 42, 43)
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/{courseId}/course").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reviews/{courseId}/course").hasRole("STUDENT") // Chỉ STUDENT mới được đánh giá
                        .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/{id}").authenticated() // OWNER_OR_ADMIN chặn ở Controller
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/{id}").authenticated()

                        // Tất cả các request không nằm trong danh sách trên đều bắt buộc login
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}