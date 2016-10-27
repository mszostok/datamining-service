package com.mszostok.repository;

import com.mszostok.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author mszostok
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}
