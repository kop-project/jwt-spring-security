package com.example.demo.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken extends TokenEntity {

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "username")
    private String username;
}
