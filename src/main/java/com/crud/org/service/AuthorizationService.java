package com.crud.org.service;

import com.crud.org.model.Organization;
import com.crud.org.model.User;
import com.crud.org.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationService {
    List<String> adminRoles = Arrays.asList("superadmin");
    List<String> userRoles = Arrays.asList("user");

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean validateRole(User user, Organization organization){
        if(adminRoles.contains(user.getRole())){
            return true;
        }

        if(userRoles.contains(user.getRole())){
            return user.getId().equals(organization.getCreatedBy());
        }

        return false;
    }

    public Optional<User> getUserInfo(String token){
        Claims claims = AuthenticationService.extractAllClaims(token);
        Optional<User> user = userRepository.findById(UUID.fromString(claims.getSubject()));
        return user;
    }

}
