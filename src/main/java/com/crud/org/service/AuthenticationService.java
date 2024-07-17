package com.crud.org.service;

import com.crud.org.model.Organization;
import com.crud.org.model.User;
import com.crud.org.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Service
public class AuthenticationService {
    private static final String SECRET_KEY = "1n14d4l4hs3cr3t";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final Long EXPIRED = 86400000L;

    List<String> adminRoles = Arrays.asList("superadmin");

    @Autowired
    UserRepository userRepository;

    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public static String hashPasswordMD5(String password) {
        try {
            // Buat objek MessageDigest untuk MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Update hash dengan password
            md.update(password.getBytes());

            // Dapatkan hasil hash dalam bentuk byte
            byte[] digest = md.digest();

            // Convert byte ke hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPasswordValid(User user, String pass ){
        String hashedReqPass = hashPasswordMD5(pass);
        return user.getPassword().equals(hashedReqPass);
    }

    public static String createToken(Map<String, Object> claims,String userId) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
