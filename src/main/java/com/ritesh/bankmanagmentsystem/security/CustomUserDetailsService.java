package com.ritesh.bankmanagmentsystem.security;

import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.ritesh.bankmanagmentsystem.entity.User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .disabled(!user.isEnabled())
            .authorities(user.getRoles().stream().map(role -> "ROLE_" + role.getName()).toArray(String[]::new))
            .build();
    }
}

