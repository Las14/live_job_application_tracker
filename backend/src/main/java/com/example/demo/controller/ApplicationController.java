package com.example.demo.controller;
import java.util.List;

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

import com.example.demo.service.ApplicationService;

import jakarta.validation.Valid;


import com.example.demo.dto.ApplicationResponse;
@RestController 
@RequestMapping ("/api/applications")
@CrossOrigin (origins = "http://localhost:4200")

public class ApplicationController {


    private final ApplicationService applicationService;
    public ApplicationController(ApplicationService applicationService){
        this.applicationService= applicationService;
    }

    @GetMapping 
    public List<ApplicationResponse> getAll(){
        return applicationService.getAll().stream()
            .map(ApplicationResponse::new)
            .toList();
    }

    @PostMapping 
    public ApplicationResponse create (@Valid @RequestBody Application application){
        return new ApplicationResponse(applicationService.create(application));
    }

    @GetMapping ("/{id}")
    public ApplicationResponse getById(@PathVariable Long id){
        return new ApplicationResponse(applicationService.getById(id));
    }

    @PutMapping ("/{id}")
    public ApplicationResponse update( @PathVariable long id,@Valid  @RequestBody Application updated){
        return new ApplicationResponse(applicationService.update(id, updated));
    }

    @DeleteMapping ("/{id}")
    public void delete(@PathVariable Long id){
        applicationService.delete(id);
    }
    
}