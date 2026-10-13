package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith (MockitoExtension.class)
public class AuthServiceTest {
    @Mock 
     private UserRepository userRepository;
     @Mock 
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks 
    private AuthService authService;
    @Mock  
    private Authentication authentication;
    @Test 
    void shouldRejectAlreadyRegisteredEmail(){
    
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(registerRequest.getEmail()))
            .thenReturn(true);
        
        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            ()-> authService.register(registerRequest)
        );
        assertEquals("Email already registered", exception.getMessage());

    
    verify(userRepository, never()).save(any());
    }

    @Test 
    void shouldRejectPasswordMismatch(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("password1234");
        registerRequest.setPasswordConfirmation("different123");

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            ()-> authService.register(registerRequest)
        );
        assertEquals("Passwords do not match", exception.getMessage());

        verify(userRepository, never()).save(any());
     
    }

    @Test 
    void shouldRejectUnderageUser(){
        RegisterRequest registerRequest = new RegisterRequest(); 
        registerRequest.setPassword("password1234");
        registerRequest.setPasswordConfirmation("password1234");
        registerRequest.setBirthDate(LocalDate.now().minusYears(14));
        

        RuntimeException exception = assertThrows(
            RuntimeException.class,
                 ()-> authService.register(registerRequest)
        );
        assertEquals("You must be at least 16 years old", exception.getMessage());
        
        verify(userRepository, never()).save(any());
    }

    @Test 
    void shouldRegisterUserSuccessfully(){
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setFirstName("Jack");
        registerRequest.setLastName("sparrow");
        registerRequest.setEmail("Jack@example.com");
        registerRequest.setPassword("password1234");
        registerRequest.setPasswordConfirmation("password1234");
        registerRequest.setBirthDate(LocalDate.now().minusYears(20));

        when(passwordEncoder.encode("password1234"))
            .thenReturn("encodePassword");

        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
            
         User result = authService.register(registerRequest);

        assertEquals("Jack@example.com", result.getEmail());
        assertEquals("encodePassword", result.getPassword());

        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Jack", savedUser.getFirstName());
        assertEquals("sparrow", savedUser.getLastName());
        assertEquals("Jack@example.com", savedUser.getEmail());
        assertEquals("encodePassword", savedUser.getPassword());
    }

    @Test 
    void shouldRejectLoginWhenEmailNotFound(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("loginTest@example.com");
    
        
        when(userRepository.findByEmail("loginTest@example.com"))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
                 ()-> authService.loginUser(loginRequest)
        );
        assertEquals("Invalid email or password", exception.getMessage());

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test 
    void shouldRejectLoginWhenPasswordIncorrect(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("loginTest@example.com");
        loginRequest.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("loginTest@example.com");
        user.setPassword("encodCorrectPassword");

        when(userRepository.findByEmail("loginTest@example.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
            "wrongPassword", "encodCorrectPassword")
        ).thenReturn(false);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                ()-> authService.loginUser(loginRequest)
            );
        assertEquals("Invalid email or password", exception.getMessage());

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test 
    void shouldLoginSuccessfully(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("loginTest@example.com");
        loginRequest.setPassword("password1234");

        User user = new User();
        user.setEmail("loginTest@example.com");
        user.setPassword("encodePassword");


        when(userRepository.findByEmail("loginTest@example.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password1234","encodePassword"))
            .thenReturn(true);

        when(jwtService.generateToken("loginTest@example.com"))
            .thenReturn("fake-jwt-token");

        String token = authService.loginUser(loginRequest);

        assertEquals("fake-jwt-token", token);

        verify(jwtService).generateToken("loginTest@example.com");
    
    }

    @Test 
    void shouldReturnAllUsers(){
        User user1 = new User();
        user1.setEmail("jack@example.com");

        User user2 = new User();
        user2.setEmail("john@example.com");

        when(userRepository.findAll())
        .thenReturn(List.of(user1, user2));

        List<User> result = authService.getALL();

       assertEquals(2, result.size());
       assertEquals("jack@example.com", result.get(0).getEmail());
       assertEquals("john@example.com", result.get(1).getEmail());

       verify(userRepository).findAll();
    }

    @Test 
    void shouldReturnCurrentAuthenticatedUser(){
        User user = new User();
        user.setEmail("jack@example.com");

        when(authentication.getName())
            .thenReturn("jack@example.com");

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        when(userRepository.findByEmail("jack@example.com"))
            .thenReturn(Optional.of(user));

        User result = authService.getCurrentUser();
        assertEquals("jack@example.com", result.getEmail());
        verify(userRepository).findByEmail("jack@example.com");
    }

    @AfterEach 
    void teatDown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectWhenCurrentUserNotFound(){
       when(authentication.getName())
            .thenReturn("unknown@example.com");

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        when(userRepository.findByEmail("unknown@example.com"))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            ()-> authService.getCurrentUser()
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test 
    void shouldUpdateCurrentUserSuccessfully(){
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("John");
        updateUserRequest.setLastName("Smith");
        updateUserRequest.setBirthDate(LocalDate.now().minusYears(25));

        User existingUser = new User();
        existingUser.setEmail("jack@example.com");
        existingUser.setFirstName("Jack");
        existingUser.setLastName("Doe");
        existingUser.setBirthDate(LocalDate.now().minusYears(20));


        when(authentication.getName())
            .thenReturn("jack@example.com");

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        when(userRepository.findByEmail("jack@example.com"))
            .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.updateCurrentUser(updateUserRequest);
      
        verify(userRepository).save(existingUser);

        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(updateUserRequest.getBirthDate(), result.getBirthDate());
        assertEquals("jack@example.com", result.getEmail());
    }
    
    @Test
    void shouldRejectUpdateWhenUnderage(){
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        updateUserRequest.setFirstName("John");
        updateUserRequest.setLastName("Smith");
        updateUserRequest.setBirthDate(LocalDate.now().minusYears(14));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
        ()-> authService.updateCurrentUser(updateUserRequest)
        );

        assertEquals("You must be at least 16 years old", exception.getMessage());
        verifyNoInteractions(userRepository);
    }







    
}
