package com.example.demo.security.jwt;

import com.example.demo.entity.Role;
import com.example.demo.entity.jwt.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(UserEntity userEntity) {
        return new JwtUser
                (
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getPassword(),
                        mapToGrantedAuthority(new ArrayList<>(userEntity.getRoleEntities()))
                );
    }

    private static List<GrantedAuthority> mapToGrantedAuthority(List<Role> roles){
        return roles.stream().map(role ->
            new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList());
    }
}
