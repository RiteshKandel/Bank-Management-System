package com.ritesh.bankmanagmentsystem.config;

import com.ritesh.bankmanagmentsystem.entity.Role;
import com.ritesh.bankmanagmentsystem.entity.RoleName;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.repository.RoleRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createRoleIfAbsent(RoleName.ADMIN);
        createRoleIfAbsent(RoleName.STAFF);
        createRoleIfAbsent(RoleName.CUSTOMER);

        if (!userRepository.existsByEmail("admin@bank.local")) {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@bank.local");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRoles(Set.of(fetchRole(RoleName.ADMIN), fetchRole(RoleName.STAFF)));
            userRepository.save(admin);
        }
    }

    private void createRoleIfAbsent(RoleName roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private Role fetchRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
    }
}

