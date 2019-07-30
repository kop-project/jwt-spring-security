package com.example.demo.entity;

import org.springframework.security.core.GrantedAuthority;
public enum Role implements GrantedAuthority {

    ACTIVE, NOT_ACTIVE, DELETED;

    @Override
    public String getAuthority() {
        return null;
    }
}
