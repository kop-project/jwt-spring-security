package com.example.demo.rest;

import com.example.demo.dto.AuthReqDto;
import com.example.demo.entity.jwt.UserEntity;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth/")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthReqDto authReqDto) {

        String username = authReqDto.getUsername();
       // authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authReqDto.getPassword()));
        UserEntity userEntity = userService.findByUserName(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User is not valid");
        }

        String token = jwtTokenProvider.createToken(username, userEntity.getRoleEntities());

        Map<Object, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
