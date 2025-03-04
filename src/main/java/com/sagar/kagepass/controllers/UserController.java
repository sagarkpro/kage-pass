package com.sagar.kagepass.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sagar.kagepass.dtos.ErrorDto;
import com.sagar.kagepass.dtos.LoginDto;
import com.sagar.kagepass.dtos.UserDto;
import com.sagar.kagepass.jwt.JwtUtils;
import com.sagar.kagepass.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.SignatureException;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUserEntity(@RequestBody UserDto userDto) {
        Optional<UserDto> user = userService.registerUser(userDto);
        if(user.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(user.get());
        }
        return ResponseEntity.badRequest().body(new ErrorDto("User already exists", null));
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
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllUserResponseEntity() {
        return ResponseEntity.ok().body(userService.getALlUsers());
    }
    
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/loggedin-user")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        var user = userService.getUserByEmail(request.getUserPrincipal().getName());
        if(user.isPresent()){
            user.get().setPassword(null);
            return ResponseEntity.ok().body(modelMapper.map(user.get(), UserDto.class));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorDto("User not found", null));
    }
}
