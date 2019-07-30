package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.jwt.UserEntity;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
public class USerServiceImpl implements UserService {

    private static Logger log = Logger.getLogger(USerServiceImpl.class.getName());


    @Autowired
    private UserRepo userRepo;

  /*  private final BCryptPasswordEncoder passwordEncoder;

    public USerServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }*/

    @Override
    public UserEntity register(UserEntity userEntity) {

        userEntity.setRoleEntities(Collections.singletonList(Role.ACTIVE));
        //userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepo.save(userEntity);

        log.info("Пользователь user: {} успешно зарегестрирован");
        return null;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserEntity findByUserName(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void delete(UserEntity userEntity) {
        userRepo.delete(userEntity);
    }
}
