package com.crud.org.controller;

import com.crud.org.model.Response;
import com.crud.org.model.User;
import com.crud.org.repository.UserRepository;
import com.crud.org.service.AuthenticationService;
import com.crud.org.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.crud.org.service.AuthenticationService.generatePassword;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody User user) {
        String pass = generatePassword(8);
        String hashedPass = AuthenticationService.hashPasswordMD5(pass);

        user.setPassword(hashedPass);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        //Send email
        String[] to = {user.getEmail()};
        String[] cc = new String[0];
        emailService.sendMail(to,cc,"Password",pass);
        savedUser.setPassword("");
        return new ResponseEntity<>(new Response("success",savedUser), HttpStatus.CREATED);
    }
}
