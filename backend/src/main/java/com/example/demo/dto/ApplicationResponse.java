package com.example.demo.dto;
import java.time.LocalDate;

import com.example.demo.entity.Application;


public class ApplicationResponse {
    private Long id;
    private String company;
    private String role;
    private String status;
    private String location;
    private LocalDate dateApplied;
    private String notes;

    public ApplicationResponse(Application app){
        this.id = app.getId();
        this.company = app.getCompany();
        this.role = app.getRole();
        this.status = app.getStatus().name();
        this.location = app.getLocation();
        this.dateApplied = app.getDateApplied();
        this.notes= app.getNotes();
    }

    public Long getId() {return id; }
    public String getCompany() {return company; }
    public String getRole() {return role;}
    public String getStatus() { return status;}
    public String getLocation(){return location;}
    public LocalDate getDateApplied() {return dateApplied;}
    public String getNotes() { return notes;}


    



    
}
