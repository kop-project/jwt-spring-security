package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Slf4j
public class USerServiceImpl implements UserService {

    private static Logger log = Logger.getLogger(USerServiceImpl.class.getName());


    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    public USerServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {

        user.setRoleEntities(Collections.singletonList(Role.ACTIVE));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        log.info("Пользователь user: {} успешно зарегестрирован");
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findByUserName(User user) {
        return userRepo.findByUsername(user.getUsername());
    }

    @Override
    public void delete(User user) {
        userRepo.delete(user);
    }
}
