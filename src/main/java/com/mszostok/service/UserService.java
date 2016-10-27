package com.mszostok.service;



import com.mszostok.domain.User;

import java.util.Collection;
import java.util.Optional;

/**
 * @author mszostok
 */
public interface UserService {

    Collection<User> getAllActiveUsers();

    Optional<User> getActiveUserByEmail(String email);

    void deactivateById(int id);

    void activateById(int id);


    Collection<User> getAllUsers();
}
