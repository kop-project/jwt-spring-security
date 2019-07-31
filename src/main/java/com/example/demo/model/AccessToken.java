package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "access_tokens")
public class AccessToken extends TokenEntity {

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "username")
    private String username;



}
