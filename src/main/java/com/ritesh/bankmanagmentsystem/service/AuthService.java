package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.auth.AuthResponse;
import com.ritesh.bankmanagmentsystem.dto.auth.LoginRequest;
import com.ritesh.bankmanagmentsystem.dto.auth.RegisterRequest;
import com.ritesh.bankmanagmentsystem.entity.Role;
import com.ritesh.bankmanagmentsystem.entity.RoleName;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.exception.BusinessException;
import com.ritesh.bankmanagmentsystem.repository.RoleRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import com.ritesh.bankmanagmentsystem.security.JwtService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email is already registered");
        }

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
            .orElseThrow(() -> new BusinessException("Default CUSTOMER role missing"));

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getRoles().add(customerRole);

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException("Invalid login credentials"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        Set<String> roles = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet());
        String token = jwtService.generateToken(user.getEmail(), Map.of("roles", roles));

        return new AuthResponse(
            token,
            "Bearer",
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            roles
        );
    }
}

