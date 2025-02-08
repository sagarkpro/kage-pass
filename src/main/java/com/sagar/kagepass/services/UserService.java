package com.sagar.kagepass.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.sagar.kagepass.dtos.LoginDto;
import com.sagar.kagepass.dtos.UserDto;
import com.sagar.kagepass.entities.User;

public interface UserService extends UserDetailsService {
    public Optional<UserDto> registerUser(UserDto userDto);
    public Optional<String> login(LoginDto loginDto);
    public Optional<User> getUserByEmail(String email);
    public List<User> getALlUsers();
}
