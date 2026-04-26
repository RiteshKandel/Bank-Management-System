package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.user.UserResponse;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.isEnabled(),
            user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
            user.getCreatedAt()
        )).toList();
    }
}

