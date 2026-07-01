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
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Cụm AUTH chung - Yêu cầu Đăng nhập (STT: 2, 3, 30)
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify", "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                        // 2. Cụm USERS (STT: 4, 5, 6, 7, 8, 9, 31)
                        // Các API quản trị user -> Chỉ ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{user_id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{user_id}/role", "/api/users/{user_id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{user_id}").hasRole("ADMIN")
                        // API cập nhật cá nhân / đổi mật khẩu (STT: 26, 27) -> OWNER hoặc ADMIN
                        // (Dùng @PreAuthorize("@userSecurity.isOwner(authentication, #userId) or hasRole('ADMIN')") ở Controller sẽ tốt hơn, ở đây chặn authenticated trước)
                        .requestMatchers(HttpMethod.PUT, "/api/users/{user_id}", "/api/users/{user_id}/password").authenticated()

                        // 3. Cụm COURSES (STT: 10, 11, 12, 13, 14, 15, 28, 29, 32)
                        // Các API lấy dữ liệu khóa học (GET) đều cần AUTH theo bảng yêu cầu
                        .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/**").authenticated()
                        // Các API thay đổi khóa học (STT: 12, 13, 14, 15) -> Chỉ ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/{course_id}", "/api/courses/{course_id}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/{course_id}").hasRole("ADMIN")

                        // 4. Cụm LESSONS (STT: 16, 17, 18, 19, 20, 21, 44)
                        // Xem bài học (GET) -> Cần AUTH
                        .requestMatchers(HttpMethod.GET, "/api/courses/{course_id}/lessons", "/api/lessons/{lesson_id}", "/api/lessons/{lesson_id}/content_preview").authenticated()
                        // Thêm, sửa, xóa bài học -> TEACHER hoặc ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/courses/{course_id}/lessons").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/lessons/{lesson_id}", "/api/lessons/{lesson_id}/publish").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lessons/{lesson_id}").hasAnyRole("TEACHER", "ADMIN")

                        // 5. Cụm ENROLLMENTS (STT: 22, 23, 24, 25) -> Chỉ STUDENT
                        .requestMatchers("/api/enrollments", "/api/enrollments/**").hasRole("STUDENT")

                        // 6. Cụm NOTIFICATIONS (STT: 33, 34, 35, 36)
                        .requestMatchers(HttpMethod.GET, "/api/notifications").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/notifications/{notification_id}/read").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/notifications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/{notification_id}").hasRole("ADMIN")

                        // 7. Cụm REPORTS (STT: 37, 38, 39) -> Chỉ ADMIN
                        .requestMatchers("/api/reports/**").hasRole("ADMIN")

                        // 8. Cụm REVIEWS (STT: 40, 41, 42, 43)
                        .requestMatchers(HttpMethod.GET, "/api/courses/{course_id}/reviews").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses/{course_id}/reviews").hasRole("STUDENT") // Sinh viên viết review
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/{review_id}").authenticated() // OWNER_OR_ADMIN xử lý ở tầng Controller
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/{review_id}").authenticated()

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