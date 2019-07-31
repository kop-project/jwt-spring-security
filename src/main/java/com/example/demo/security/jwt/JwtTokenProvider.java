package com.example.demo.security.jwt;

import com.example.demo.dto.RefreshTokenDto;
import com.example.demo.model.*;
import com.example.demo.repository.AccessTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Util class that provides methods for generation, validation, etc. of JWT token.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

@Component
public class JwtTokenProvider {

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.access_token.expired}")
    private long validityAccessInMilliseconds;

    @Value("${jwt.refresh_token.expired}")
    private long validityRefreshInMilliseconds;


    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Object createToken(String username, List<Role> roles, String typeToken) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", getRoleNames(roles));

        Date now = new Date();
        Date validity;
        AccessToken accessToken = new AccessToken();
        RefreshToken refreshToken = new RefreshToken();

        if ("access".equals(typeToken)) {
            validity = new Date(now.getTime() + validityAccessInMilliseconds);
            accessToken.setUsername(username);
            accessToken.setCreated(now);
            accessToken.setUpdated(validity);
        } else {
            validity = new Date(now.getTime() + validityRefreshInMilliseconds);
            refreshToken.setUsername(username);
            refreshToken.setCreated(now);
            refreshToken.setUpdated(validity);
        }

        String token = Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secret)//
                .compact();

        if ("access".equals(typeToken)) {
            accessToken.setAccessToken(token);
        } else {
            refreshToken.setRefreshToken(token);
        }

        return accessToken.getAccessToken() != null ? accessToken : refreshToken;
    }

    public Object checkToken(RefreshToken refreshToken) {
        Date now = new Date();

        if (now.getTime() < refreshToken.getUpdated().getTime()) {
            return  refreshToken;
        } else {
            return null;
        }
    }

    public String resultTokenMap(RefreshTokenDto token) throws IOException {

        String base64EncodedBody = token.toString().split("\\.")[1];
        org.apache.tomcat.util.codec.binary.Base64 base64Url = new org.apache.tomcat.util.codec.binary.Base64(true);
        //token_body
        String body = new String(base64Url.decode(base64EncodedBody));
        HashMap resultTokenMap = new ObjectMapper().readValue(body, HashMap.class);
        return resultTokenMap.get("sub").toString();
    }


    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> result = new ArrayList<>();

        userRoles.forEach(role -> {
            result.add(role.getName());
        });

        return result;
    }
}
