package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class RegisterRequest {

   @NotBlank
   private String firstName;
   @NotBlank
   private String lastName;
   @NotBlank
   private String email;
   @NotNull(message = "Birth date is required")
   @Past(message = "Birth date must be in the past")
   private LocalDate birthDate;
   @NotBlank(message = "Password should be atleast 6 characters")
   private String password;
   @NotBlank
   private String passwordConfirmation;

   
   public String getFirstName() {return firstName;}
   public void setFirstName(String firstName) {this.firstName = firstName;}

   public String getLastName() {return lastName;}
   public void setLastName(String lastName) {this.lastName = lastName;}

   public String getEmail() {return email;}
   public void setEmail(String email) {this.email = email;}

   public String getPassword() {return password;}
   public void setPassword(String password) {this.password = password;}

   public LocalDate getBirthDate() {return birthDate;}
   public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}

   public String getPasswordConfirmation() {return passwordConfirmation;}
   public void setPasswordConfirmation(String passwordConfirmation) {this.passwordConfirmation = passwordConfirmation;}
    
}
