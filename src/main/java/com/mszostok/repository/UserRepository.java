package com.mszostok.repository;

import com.mszostok.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

  Collection<User> findByActiveTrue();

  Optional<User> findOneByEmailAndActiveTrue(String email);

  Optional<User> findOneByEmail(String email);

  Optional<User> findOneByUsername(String username);

  User findOneByIdUser(Integer id);
}
