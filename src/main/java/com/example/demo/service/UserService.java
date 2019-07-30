package com.example.demo.service;

import com.example.demo.entity.jwt.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity register(UserEntity userEntity);
    List<UserEntity> getAllUsers();
    UserEntity findByUserName(String username);
    void delete(UserEntity userEntity);

}
