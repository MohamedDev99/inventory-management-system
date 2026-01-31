package com.moeware.ims.repository;

import com.moeware.ims.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity with custom queries
 *
 * @author MoeWare Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     *
     * @param username Username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     *
     * @param email Email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     *
     * @param username Username
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     *
     * @param email Email address
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users
     *
     * @return List of active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find active users with pagination
     *
     * @param pageable Pagination information
     * @return Page of active users
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Find users by role name
     *
     * @param roleName Role name
     * @return List of users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Find active users by role name with pagination
     *
     * @param roleName Role name
     * @param pageable Pagination information
     * @return Page of users
     */
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.isActive = true")
    Page<User> findActiveByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Search users by username or email (case-insensitive)
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND u.isActive = true")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count active users
     *
     * @return Number of active users
     */
    long countByIsActiveTrue();

    /**
     * Count users by role
     *
     * @param roleName Role name
     * @return Number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
}