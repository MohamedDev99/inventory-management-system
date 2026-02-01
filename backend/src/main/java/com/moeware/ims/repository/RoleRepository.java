
package com.moeware.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.Role;

import java.util.Optional;

/**
 * Repository for Role entity
 *
 * @author MoeWare Team
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     *
     * @param name Role name (ADMIN, MANAGER, etc.)
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     *
     * @param name Role name
     * @return true if role exists
     */
    boolean existsByName(String name);
}