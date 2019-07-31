package com.example.demo.repository;

import com.example.demo.model.AccessToken;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface that extends {@link JpaRepository} for class {@link User}.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUsername(String username);
}
