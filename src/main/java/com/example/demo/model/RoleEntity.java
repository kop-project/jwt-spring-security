package com.example.demo.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role")
@Data
public class RoleEntity extends BaseEntity{

    private String name;

}
