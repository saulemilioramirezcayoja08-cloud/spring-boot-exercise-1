package com.exercise_1.modules.auth.repository;

import com.exercise_1.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT r.code FROM UserRole ur JOIN ur.role r WHERE ur.user.id = :userId")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
}