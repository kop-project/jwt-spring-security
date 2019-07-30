package com.example.demo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name ="users")
@Data
public class User extends BaseEntity {

    private String username;
    private String password;

    private List<RoleEntity> roleEntities;
}
