package com.mszostok.service;


import com.mszostok.domain.User;
import com.mszostok.domain.UserRole;
import com.mszostok.repository.UserRepository;
import com.mszostok.repository.UserRoleRepository;
import com.mszostok.utils.SecurityUtils;
import com.mszostok.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Transactional
@Service("userService")
public class UserService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserRoleRepository userRoleRepository;

  private void setActive(final int id, final boolean active) {
    User user = userRepository.findOneByIdUser(id);

    user.setActive(active); //automatic update due to Transactional annotation
  }

  @Transactional(readOnly = true)
  public Collection<User> getAllActiveUsers() {
    return userRepository.findByActiveTrue();
  }

  @Transactional(readOnly = true)
  public Optional<User> getActiveUserByEmail(final String email) {
    return userRepository.findOneByEmailAndActiveTrue(email);
  }

  @Transactional(readOnly = true)
  public User getCurrentLoggedUser() {
    return SecurityUtils.getCurrentUserLogin().map(email ->
      getActiveUserByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
    ).orElseThrow(() -> new IllegalStateException("Cannot get logged user"));
  }

  public void deactivateById(final int id) {
    setActive(id, false);
  }

  public void activateById(final int id) {
    setActive(id, true);
  }

  @Transactional(readOnly = true)
  public Collection<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User createSimpleUser(final UserDto userDto) {
    User user = new User();
    UserRole userRole = new UserRole("USER", user);

    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

    user.setRoles(Collections.singletonList(userRole));
    user.setActive(true);

    userRepository.save(user);
    userRoleRepository.save(userRole);

    return user;
  }

  public boolean isUsernameInUse(final User actual, final UserDto update) {
    return !update.getUsername().equals(actual.getUsername()) && userRepository.findOneByUsername(update.getUsername()).isPresent();
  }


  public boolean isEmailInUse(final User actual, final UserDto update) {
    return !update.getEmail().equals(actual.getEmail()) && userRepository.findOneByEmail(update.getEmail()).isPresent();

  }

  public User updateProfile(final UserDto userDto) {
    String email = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Cannot get logged user"));

    return userRepository.findOneByEmailAndActiveTrue(email)
      .map(user -> {
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setCity(userDto.getCity());
        user.setCountry(userDto.getCountry());
        user.setPostalCode(userDto.getPostalCode());

        return user;
      })
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
  }
}
