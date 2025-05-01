package com.collegeelection.service;

import com.collegeelection.model.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    void saveUser(User user);
    List<User> getAllUsers();
}
