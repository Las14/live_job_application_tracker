package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.dto.AuthResponse;




import jakarta.validation.Valid;




@RestController 
@RequestMapping ("/api/users")
public class AuthController {

    private final AuthService authService;
    
    

    public AuthController(AuthService authService){
        this.authService = authService;
       
       
    }

    @PostMapping ("/register")
    public ResponseEntity<?> register(
        @Valid @RequestBody RegisterRequest request){
         
            try{
                 authService.register(request);
                
                    return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully");
                } catch (RuntimeException e){
                    return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
                
            }
        }
    @GetMapping 
    public List<User>getAll(){
        return authService.getALL();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        try{
           String token = authService.loginUser(request);
            return ResponseEntity.ok(new AuthResponse (token));
        }catch (RuntimeException e){
            return ResponseEntity
            .badRequest()
            .body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public User getProfile(){
        return authService.getCurrentUser();
    }

    @PutMapping("/profile")
    public User updateProfile(@RequestBody UpdateUserRequest request){
        return authService.updateCurrentUser(request);
    }

    
}
