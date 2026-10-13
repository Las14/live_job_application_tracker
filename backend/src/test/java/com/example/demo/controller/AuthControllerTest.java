package com.example.demo.controller;

import com.example.demo.service.AuthService;
import com.example.demo.security.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.example.demo.dto.LoginRequest;

import com.example.demo.entity.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired 
    private MockMvc mockMvn; 

    @MockitoBean 
    private  AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test 
    void shouldRegisterUserSuccessfully() throws Exception {
        String json = """
                {
                    "firstName":"Jack",
                    "lastName":"Sparrow",
                    "email":"jack@example.com",
                    "password":"password1234",
                    "passwordConfirmation":"password1234",
                    "birthDate":"2000-01-01"
                }
                """;

        mockMvn.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(content().string("User registered successfully"));
    }

    @Test 
    void shouldLoginUserSuccessfully() throws Exception{

        String json = """
                {
                        "email":"jack@example.com",
                        "password":"password1234"
                }
                """;

        when(authService.loginUser(any(LoginRequest.class)))
            .thenReturn("fake-jwt-token");

        
        mockMvn.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test 
    void shouldGetUserProfileSuccessfully() throws Exception{
        User jack = new User();
        jack.setFirstName("Jack");
        jack.setLastName("Sparrow");
        jack.setEmail("jack@example.com");
        jack.setPassword("must-not-leak");

        when(authService.getCurrentUser()).thenReturn(jack);

        mockMvn.perform(get(("/api/users/profile")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jack"))
            .andExpect(jsonPath("$.lastName").value("Sparrow"))
            .andExpect(jsonPath("$.email").value("jack@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

}
