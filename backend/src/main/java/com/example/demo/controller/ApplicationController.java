package com.example.demo.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Application;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;

import com.example.demo.entity.User;
import com.example.demo.dto.ApplicationResponse;
@RestController 
@RequestMapping ("/api/applications")
@CrossOrigin (origins = "http://localhost:4200")

public class ApplicationController {
    private final ApplicationRepository repository ;
    private final UserRepository userRepository;
    public ApplicationController(ApplicationRepository repository, UserRepository userRepository){
        this.repository= repository;
        this.userRepository= userRepository;
    }

    private User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<ApplicationResponse> getAll() {
        User currentUser = getCurrentUser();
        return repository.findByUserOrderByDateAppliedDesc(currentUser).stream()
        .map(ApplicationResponse::new)
        .toList();
    }
    @PostMapping 
    public ApplicationResponse create (@Valid @RequestBody Application application){
       application.setUser(getCurrentUser());
       Application saved = repository.save(application);
        return new ApplicationResponse(saved);

    }

    @GetMapping ("/{id}")
    public ApplicationResponse getById(@PathVariable Long id){
        Application app = repository.findByIdAndUser(id, getCurrentUser())
                 .orElseThrow(() -> new RuntimeException("Application not found"));
        return new ApplicationResponse(app);
    }

    @PutMapping ("/{id}")
    public ApplicationResponse update( @PathVariable long id,@Valid  @RequestBody Application updated){
        Application existing  = repository.findByIdAndUser(id, getCurrentUser())
             .orElseThrow(() -> new RuntimeException("Application not found"));
        updated.setId(existing.getId());
        updated.setUser(existing.getUser());
        Application saved = repository.save(updated);
        return new ApplicationResponse(saved);
    }
    @DeleteMapping ("/{id}")
    public void delete(@PathVariable Long id){
        Application existing = repository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new RuntimeException("Application not found"));
        repository.delete(existing);
    }
    
}