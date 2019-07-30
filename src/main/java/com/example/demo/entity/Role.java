package com.example.demo.entity;

import org.springframework.security.core.GrantedAuthority;
public enum Role implements GrantedAuthority {

    ACTIVE, NOT_ACTIVE, DELETED;

    public String name;

    @Override
    public String getAuthority() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
