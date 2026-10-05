package com.example.demo.service;

import com.example.demo.entity.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;



@Service 

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
   


    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtService = jwtService;
    }
    public User register(RegisterRequest request){
       
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if(!request.getPassword()
            .equals(request.getPasswordConfirmation())){

         throw new RuntimeException("Password do not match");
        }
        
        validateAge(request.getBirthDate());
    
      
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setBirthDate(request.getBirthDate());
        user.setPassword(passwordEncoder.encode(request.getPassword())
     );
     return userRepository.save(user);
     
    }

    public List<User> getALL(){
        return userRepository.findAll();
    }

    public String loginUser(LoginRequest request){
      User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new RuntimeException("invalid Email or Password"));

      if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
        throw new RuntimeException("invalid Email or Password");
      }
      return jwtService.generateToken(user.getEmail());
  
    }

   public User getCurrentUser(){

        String email = SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getName();

        return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public User updateCurrentUser(UpdateUserRequest request){
         validateAge(request.getBirthDate());
        User user = getCurrentUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        return userRepository.save(user);
    }

    private void validateAge(LocalDate birthDate){
        LocalDate minimumBirthDate = LocalDate.now().minusYears(16);
        if(birthDate.isAfter(minimumBirthDate)){
            throw new RuntimeException("You must be at least 16 years old");
        }
    }

}
