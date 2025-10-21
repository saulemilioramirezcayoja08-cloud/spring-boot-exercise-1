package com.exercise_1.modules.auth.repository;

import com.exercise_1.modules.auth.entity.UserRole;
import com.exercise_1.modules.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}