package com.exercise_1.modules.auth.repository;

import com.exercise_1.modules.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}