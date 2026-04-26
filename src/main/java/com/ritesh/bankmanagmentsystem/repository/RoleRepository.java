package com.ritesh.bankmanagmentsystem.repository;

import com.ritesh.bankmanagmentsystem.entity.Role;
import com.ritesh.bankmanagmentsystem.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

