package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    List<User> getAllUsers();
    User findByUserName(User user);
    void delete(User user);

}
