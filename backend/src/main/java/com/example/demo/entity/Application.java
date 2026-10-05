package com.example.demo.entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;


@Entity
@Table(name = "application")

public class Application {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank (message = "Role is requierd")
    private String role;
    @NotBlank (message = "Company is requierd")
    private String company;
    @NotBlank (message = "Location is requierd")
    private String location; 
    @NotNull (message = "Date applied is requierd")
    @PastOrPresent(message = "Date applied cannot be in the future")
    private LocalDate dateApplied;
    private String notes;
    @ManyToOne 
    @JoinColumn(name= "user_id", nullable = false)
    private User user;
   
    
    @Enumerated (EnumType.STRING)
    private Status status;

    public enum Status{
        APPLIED, INTERVIEW, OFFER, REJECTED
    }

    public long getId() { return id;}
    public void setId(long id) {this.id = id; }

    public String getCompany() {return company;}
    public void setCompany(String company) {this.company = company; }

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) {this.status = status;}

    public String getLocation() { return location;}
    public void setLocation(String location) {this.location = location;}

    public LocalDate getDateApplied() { return dateApplied;}
    public void setDateApplied(LocalDate dateApplied) {this.dateApplied = dateApplied;}

    public String getNotes() { return notes;}
    public void setNotes(String notes) {this.notes = notes;}
   
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    

}