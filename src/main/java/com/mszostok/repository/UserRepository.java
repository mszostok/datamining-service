package com.mszostok.repository;

import com.mszostok.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * @author mszostok
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    Collection<User> findByActiveTrue();

    Optional<User> findOneByeMailAndActiveTrue(String email);

    User findOneByIdUser(Integer id);
}
