package com.edu.cms.common.principal;

import com.edu.cms.entity.User;
import com.edu.cms.common.enums.UserRole;
import com.edu.cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User users = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tồn tại emaik: " + email));

        return CustomUserDetail.builder()
                .email(users.getEmail())
                .passwordHash(users.getPasswordHash())
                .fullName(users.getFullName())
                .email(users.getEmail())
                .isActive(users.getIsActive())
                .authorities(mapToGrandAuthority(users.getRole()))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapToGrandAuthority(UserRole role) {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }
}
