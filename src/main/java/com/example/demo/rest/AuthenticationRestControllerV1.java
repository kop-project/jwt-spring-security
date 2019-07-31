package com.example.demo.rest;

import com.example.demo.dto.AuthenticationRequestDto;
import com.example.demo.dto.RefreshTokenDto;
import com.example.demo.model.AccessToken;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.AccessTokenRepository;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for authentication requests (login, logout, register, etc.)
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@RestController
@RequestMapping(value = "/api/auth/")
public class AuthenticationRestControllerV1 {

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;


    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);
            AccessToken accessToken;
            RefreshToken refreshToken;
            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            } else {
                accessToken = (AccessToken) jwtTokenProvider.createToken(username, user.getRoles(), "access");
                refreshToken = (RefreshToken) jwtTokenProvider.createToken(username, user.getRoles(), "refresh");
            }

            Map<Object, Object> response = new HashMap<>();
            response.put("access_token", accessToken.getAccessToken());
            response.put("refresh_token", refreshToken.getRefreshToken());
            response.put("expires_in", accessToken.getUpdated().getTime());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("refresh-token")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenDto token) throws IOException {

        String username = jwtTokenProvider.resultTokenMap(token);
        User user = userService.findByUsername(username);
        AccessToken accessToken;
        RefreshToken refreshToken;
        if (user != null) {
            refreshToken = refreshTokenRepository.findByUsername(user.getUsername());
            //jwtTokenProvider.
            if (token.getToken().equals(refreshToken.getRefreshToken())) {
                if (jwtTokenProvider.checkToken(refreshToken) != null) {

                    accessToken = (AccessToken) jwtTokenProvider.createToken(username, user.getRoles(), "access");
                    refreshToken = (RefreshToken) jwtTokenProvider.createToken(username, user.getRoles(), "refresh");

                    Map<Object, Object> response = new HashMap<>();

                    response.put("access_token", accessToken.getAccessToken());
                    response.put("refresh_token", refreshToken.getRefreshToken());
                    response.put("expires_in", accessToken.getUpdated().getTime());
                    return ResponseEntity.ok(response);
                }
            }
        }

        return null;
    }
}
