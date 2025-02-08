package com.sagar.kagepass.services;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sagar.kagepass.dtos.LoginDto;
import com.sagar.kagepass.dtos.UserDto;
import com.sagar.kagepass.entities.User;
import com.sagar.kagepass.jwt.JwtUtils;
import com.sagar.kagepass.respositories.UserRepository;


@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent()){
            return user.get();
        }
        throw new UsernameNotFoundException("Cant find user");
    }

    @Override
    public Optional<UserDto> registerUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if(existingUser.isPresent()){
            return Optional.empty();
        }
        User user = modelMapper.map(userDto, User.class);
        String encryptedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        return Optional.of(userDto);
    }

    @Override
    public Optional<String> login(LoginDto loginDto) {
        Optional<User> existingUser = userRepository.findByEmail(loginDto.getEmail());
        if(existingUser.isPresent() && bCryptPasswordEncoder.matches(loginDto.getPassword(), existingUser.get().getPassword())){
            return Optional.of(jwtUtils.generateToken(existingUser.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getALlUsers() {
        return userRepository.findAll();
    }  
}
