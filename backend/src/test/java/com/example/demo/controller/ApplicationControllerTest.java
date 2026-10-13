package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;


import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.eq;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.example.demo.entity.Application;

import com.example.demo.security.JwtService;
import com.example.demo.service.ApplicationService;

@WithMockUser (username = "jack@example.com")
@WebMvcTest(ApplicationController.class) 
public class ApplicationControllerTest {

    @Autowired 
    private MockMvc mockMvn; 

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private JwtService jwtService;

    
    private Application application(long id, String company){
        Application application = new Application();
        application.setId(id);
        application.setCompany(company);
        application.setRole("Java Developer");
        application.setStatus(Application.Status.APPLIED);
        application.setDateApplied(LocalDate.of(2000,1,1));
        application.setLocation("Vienna");
        application.setNotes("Remote job");
        return application;
    }

    private String request(String company, String status, String date){
        return """
                {
                    "company":"%s", 
                    "role":"Java Developer", 
                    "status":"%s", 
                    "dateApplied":"%s",
                    "location":"Vienna",
                    "notes":"Remote job"
                }
                """.formatted(company, status, date);
    }
    @Test 
    void shouldReturnCurrentUserApplications() throws Exception{

        when(applicationService.getAll()).thenReturn(List.of(
            application(1L, "Google"), application(2L, "Amazon")
        ));

        mockMvn.perform(get("/api/applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].company").value("Google"))
            .andExpect(jsonPath("$[1].company").value("Amazon"));
        
    }

    @Test 
    void shouldReturnEmptyListWhenUserHasNoApplications() throws Exception{

        when(applicationService.getAll()).thenReturn(List.of());

        mockMvn.perform(get("/api/applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
        
    }

    @Test 
    void shouldReturnApplicationById() throws Exception{
         when(applicationService.getById(1L)).thenReturn(application(1L, "Google"));

        mockMvn.perform(get("/api/applications/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.company").value("Google"))
            .andExpect(jsonPath("$.role").value("Java Developer"))
            .andExpect(jsonPath("$.status").value("APPLIED"))
            .andExpect(jsonPath("$.dateApplied").value("2000-01-01"))
            .andExpect(jsonPath("$.location").value("Vienna"))
            .andExpect(jsonPath("$.notes").value("Remote job"))
            .andExpect(jsonPath("$.user").doesNotExist());
       
    }


    @Test 
    void shouldCreateApplicationSuccessfully() throws Exception{
        when(applicationService.create(any(Application.class)))
            .thenReturn(application(1L, "Google"));


        mockMvn.perform(post("/api/applications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request("Google", "APPLIED", "2000-01-01")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.company").value("Google"))
            .andExpect(jsonPath("$.role").value("Java Developer"));

        ArgumentCaptor<Application>captor = ArgumentCaptor.forClass(Application.class);

        verify(applicationService).create(captor.capture());
        assertEquals("Google", captor.getValue().getCompany());
        assertEquals(Application.Status.APPLIED, captor.getValue().getStatus());
        assertEquals(LocalDate.of(2000,1,1), captor.getValue().getDateApplied());
    }

    @Test 
    void shouldRejectApplicationWithEmptyCompany() throws Exception{

        mockMvn.perform(post("/api/applications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request("", "APPLIED", "2000-01-01")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.company").value("Company is requierd"));

        verifyNoInteractions(applicationService);
               
    }

    @Test 
    void shouldUpdateApplicationSuccessfully() throws Exception{

        Application saved = application(1L,"Google");
        saved.setStatus(Application.Status.INTERVIEW);

        when(applicationService.update(eq(1L), any(Application.class))).thenReturn(saved);

            mockMvn.perform(put("/api/applications/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request("Google", "INTERVIEW", "2000-01-01")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.company").value("Google"))
            .andExpect(jsonPath("$.status").value("INTERVIEW"));

            ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);

            verify(applicationService).update(eq(1L),captor.capture());
            assertEquals(Application.Status.INTERVIEW, captor.getValue().getStatus());
    }
    
    @Test 
    void shouldDeleteApplicationSuccessfully() throws Exception{

        mockMvn.perform(delete("/api/applications/1")
                .with(csrf()))
            .andExpect(status().isOk());

        verify(applicationService).delete(1L);
    }

   
    @Test
    void shouldRejectMissingStatus() throws Exception {

        mockMvn.perform(post("/api/applications")
                .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "company":"Google",
                    "role":"Developer",
                    "location":"Vienna",
                    "dateApplied":"2000-01-01"
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("Status is required"));
            
        verifyNoInteractions(applicationService);
    }

    @Test
    void shouldRejectInvalidStatus() throws Exception {

        mockMvn.perform(post("/api/applications")
                .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request("Google", "UNKNOWN", "2000-01-01")))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
    }

    @Test 
    void shouldRejectFutureDate() throws Exception{

        mockMvn.perform(post("/api/applications")
                .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request("Google", "APPLIED", "2999-01-01")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.dateApplied").exists());

        verifyNoInteractions(applicationService);

    }

    @Test 
    void shouldReturnBadRequestWhenDeleteIsUnavailble() throws Exception{
        doThrow(new RuntimeException("Application not found")).when(applicationService).delete(2L);

         mockMvn.perform(delete("/api/applications/2")
            .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Application not found"));

            verify(applicationService).delete(2L);

    

    }

    @Test 
    void shouldReturnBadRequestWhenUpdateIsUnavailable() throws Exception{
        when(applicationService.update(eq(2L), any(Application.class)))
            .thenThrow(new RuntimeException("Application not found"));

            mockMvn.perform(put("/api/applications/2")
                .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request("Google", "INTERVIEW", "2000-01-01")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Application not found"));
        verify(applicationService).update(eq(2L), any(Application.class));

    }




}
