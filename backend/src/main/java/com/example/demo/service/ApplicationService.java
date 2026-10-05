
package com.example.demo.service;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.Application;
@Service 



public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

     public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(){

        String email = SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getName();

        return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public Application create(Application application){
        User user = getCurrentUser();
        application.setUser(user);
        return applicationRepository.save(application);
    }

    public List<Application>getAll(){
        User user = getCurrentUser();
        return applicationRepository.findByUserOrderByDateAppliedDesc(user);

    }
}