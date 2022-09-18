package com.tchat.ms_authentification.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tchat.ms_authentification.constant.JwtConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class JwtUtil implements Serializable {

    private static final long serialVersionUID = 234234523523L;

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {
        List<String> roles=new ArrayList<>();
        userDetails.getAuthorities().forEach(a->{
            roles.add(a.getAuthority());
        });
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withArrayClaim("roles",roles.toArray(new String[roles.size()]))
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtConstant.JWT_TOKEN_VALIDITY * 1000))
                .sign(Algorithm.HMAC512(secretKey));
    }

}