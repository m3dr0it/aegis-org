package com.crud.org.controller;

import com.crud.org.model.LoginRequest;
import com.crud.org.model.Response;
import com.crud.org.model.User;
import com.crud.org.repository.UserRepository;
import com.crud.org.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;


    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if(user == null){
            return new ResponseEntity<>(new Response("User not found", null), HttpStatus.NOT_FOUND);
        }

        boolean isValid = AuthenticationService.isPasswordValid(user,loginRequest.getPassword());

        if(!isValid){
            return new ResponseEntity<>(new Response("Wrong password", null), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        claims.put("role",user.getRole());

        Map<String,String> data = new HashMap<>();
        data.put("token", AuthenticationService.createToken(claims,user.getId().toString()));

        return new ResponseEntity<>(new Response("success", data), HttpStatus.CREATED);
    }
}
