package com.mszostok.service;


import com.mszostok.domain.User;
import com.mszostok.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  private void setActive(final int id, final boolean active) {
    User user = userRepository.findOneByIdUser(id);

    user.setActive(active); //automatic update due to Transactional annotation
  }

  @Override
  public Collection<User> getAllActiveUsers() {
    return userRepository.findByActiveTrue();
  }

  @Override
  public Optional<User> getActiveUserByEmail(final String email) {
    return userRepository.findOneByEmailAndActiveTrue(email);
  }

  @Override
  public void deactivateById(final int id) {
    setActive(id, false);
  }

  @Override
  public void activateById(final int id) {
    setActive(id, true);
  }

  @Override
  public Collection<User> getAllUsers() {
    return userRepository.findAll();
  }

}
