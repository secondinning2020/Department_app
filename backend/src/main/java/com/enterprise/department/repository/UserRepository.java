package com.enterprise.department.repository;

import com.enterprise.department.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find user by username
     *
     * @param username Username
     * @return Optional user
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user exists by username
     *
     * @param username Username
     * @return true if exists
     */
    boolean existsByUsername(String username);
}
