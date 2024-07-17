package com.crud.org.controller;

import com.crud.org.model.Organization;
import com.crud.org.model.Response;
import com.crud.org.model.User;
import com.crud.org.repository.OrganizationRepository;
import com.crud.org.repository.UserRepository;
import com.crud.org.service.AuthenticationService;
import com.crud.org.service.AuthorizationService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Response> create(HttpServletRequest request,
                                           @RequestBody Organization organization) {
        String authHeader = request.getHeader("Authorization");
        String id = getUserIdFromToken(authHeader);

        if(id.isEmpty()){
            return new ResponseEntity<>(new Response("unauthorized",null), HttpStatus.UNAUTHORIZED);
        }

        organization.setCreatedBy(UUID.fromString(id));
        organization.setUpdatedBy(UUID.fromString(id));
        Organization saved = organizationRepository.save(organization);
        return new ResponseEntity<>(new Response("success",saved), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response> update(HttpServletRequest request,
            @RequestBody Organization newOrganizatoin) {
        String authHeader = request.getHeader("Authorization");
        String id = getUserIdFromToken(authHeader);

        if(id.isEmpty()){
            return new ResponseEntity<>(new Response("unauthorized",null), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> user = userRepository.findById(UUID.fromString(id));
        Optional<Organization> organization = organizationRepository.findById(newOrganizatoin.getId());

        if(!organization.isPresent()){
            return new ResponseEntity<>(new Response("organization not found",null), HttpStatus.NOT_FOUND);
        }

        if(!authorizationService.validateRole(user.get(),organization.get())){
            return new ResponseEntity<>(new Response("unauthorized",null), HttpStatus.UNAUTHORIZED);
        }

        Organization organization1 = organization.get();
        organization1.setName(newOrganizatoin.getName());
        organization1.setUpdatedBy(user.get().getId());
        organization1.setUpdatedAt(LocalDateTime.now());
        organizationRepository.save(organization1);

        return new ResponseEntity<>(new Response("success",null), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Response> delete(HttpServletRequest request,
                                           @RequestBody String organizationId) {
        String authHeader = request.getHeader("Authorization");
        String id = getUserIdFromToken(authHeader);

        if(id.isEmpty()){
            return new ResponseEntity<>(new Response("unauthorized",null), HttpStatus.UNAUTHORIZED);
        }

        Optional<User> user = userRepository.findById(UUID.fromString(id));
        Optional<Organization> organization = organizationRepository.findById(UUID.fromString(organizationId));

        if(!organization.isPresent()){
            return new ResponseEntity<>(new Response("organization not found",null), HttpStatus.NOT_FOUND);
        }

        if(!authorizationService.validateRole(user.get(),organization.get())){
            return new ResponseEntity<>(new Response("unauthorized",null), HttpStatus.UNAUTHORIZED);
        }

        organizationRepository.deleteById(UUID.fromString(organizationId));

        return new ResponseEntity<>(new Response("success",null), HttpStatus.OK);
    }


    private String getUserIdFromToken(String authHeader){
        String id = "";

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Claims claims = AuthenticationService.extractAllClaims(token);
            id = claims.getSubject();
        }
        return id;
    }
}
