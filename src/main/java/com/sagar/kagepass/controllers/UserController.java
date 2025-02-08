package com.sagar.kagepass.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sagar.kagepass.dtos.LoginDto;
import com.sagar.kagepass.dtos.UserDto;
import com.sagar.kagepass.jwt.JwtUtils;
import com.sagar.kagepass.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.security.SignatureException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUserEntity(@RequestBody UserDto userDto) {
        Optional<UserDto> user = userService.registerUser(userDto);
        if(user.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(user.get());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        var token = userService.login(loginDto);
        if(token.isPresent()){
            return ResponseEntity.ok().body(Map.of("token", token.get()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) .body(Map.of("error", "Invalid email or password"));
    }
    

    @PostMapping("/verifyToken")
    public boolean verifyToken(@RequestBody String token) throws SignatureException {
        return jwtUtils.verifySignature(token);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/allUsers")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllUserResponseEntity() {
        return ResponseEntity.ok().body(userService.getALlUsers());
    }
    
    
        
    
    
}
