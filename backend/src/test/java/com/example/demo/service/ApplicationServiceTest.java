package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.entity.Application;
import com.example.demo.entity.User;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User jack;

    @BeforeEach
    void setUp() {
        jack = new User();
        jack.setEmail("jack@example.com");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("jack@example.com", null, List.of()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void currentUserExists() {
        when(userRepository.findByEmail("jack@example.com")).thenReturn(Optional.of(jack));
    }

    private Application application(long id, User owner) {
        Application application = new Application();
        application.setId(id);
        application.setCompany("Google");
        application.setRole("Java Developer");
        application.setStatus(Application.Status.APPLIED);
        application.setDateApplied(LocalDate.of(2000, 1, 1));
        application.setLocation("Vienna");
        application.setUser(owner);
        return application;
    }

    @Test
    void shouldCreateApplicationForCurrentUser() {
        currentUserExists();
        User suppliedOwner = new User();
        Application submitted = application(99L, suppliedOwner);
        when(applicationRepository.save(any(Application.class)))
            .thenAnswer(call -> call.getArgument(0));

        Application result = applicationService.create(submitted);

        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(captor.capture());

        assertSame(jack, captor.getValue().getUser());
        assertEquals(0L, captor.getValue().getId());
        assertSame(submitted, result);
    }

   
    @Test
    void shouldReturnOnlyCurrentUserApplications() {
        currentUserExists();
        Application google = application(1L, jack);
        when(applicationRepository.findByUserOrderByDateAppliedDesc(jack))
            .thenReturn(List.of(google));

        assertEquals(List.of(google), applicationService.getAll());

        verify(applicationRepository).findByUserOrderByDateAppliedDesc(jack);
    }


  

    @Test
    void shouldFindApplicationByIdAndCurrentUser() {
        currentUserExists();
        Application existing = application(1L, jack);
        when(applicationRepository.findByIdAndUser(1L, jack))
            .thenReturn(Optional.of(existing));

        assertSame(existing, applicationService.getById(1L));

        verify(applicationRepository).findByIdAndUser(1L, jack);
        
    }

    @Test
    void shouldRejectReadWhenApplicationDoesNotBelongToCurrentUserOrDoesNotExist() {
        currentUserExists();

        when(applicationRepository.findByIdAndUser(2L, jack))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
             () -> applicationService.getById(2L));

        assertEquals("Application not found", ex.getMessage());

        verify(applicationRepository).findByIdAndUser(2L, jack);
        
    }

    @Test
    void shouldUpdateApplicationPreservingStoredIdAndOwner() {
        currentUserExists();
        Application existing = application(1L, jack);

        Application updated = application(999L, new User());
        updated.setCompany("Amazon");
        updated.setRole("Backend Developer");
        updated.setStatus(Application.Status.INTERVIEW);
        updated.setLocation("Berlin");
        updated.setDateApplied(LocalDate.of(2000, 2, 1));
        updated.setNotes("Updated notes");

        when(applicationRepository.findByIdAndUser(1L, jack)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(any(Application.class))).thenAnswer(call -> call.getArgument(0));

        Application result = applicationService.update(1L, updated);

        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);

        verify(applicationRepository).save(captor.capture());

        Application saved = captor.getValue();

        assertEquals(1L, saved.getId());
        assertSame(jack, saved.getUser());
        assertEquals("Amazon", saved.getCompany());
        assertEquals("Backend Developer", saved.getRole());
        assertEquals(Application.Status.INTERVIEW, saved.getStatus());
        assertEquals("Berlin", saved.getLocation());
        assertEquals(LocalDate.of(2000, 2, 1), saved.getDateApplied());
        assertEquals("Updated notes", saved.getNotes());
        assertSame(updated, result);
    }

    @Test
    void shouldNotUpdateUnavailableApplication() {
        currentUserExists();
        when(applicationRepository.findByIdAndUser(2L, jack))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> applicationService.update(2L, new Application()));

        assertEquals("Application not found", ex.getMessage());

        verify(applicationRepository).findByIdAndUser(2L, jack);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void shouldDeleteCurrentUsersApplication() {
        currentUserExists();
        Application existing = application(1L, jack);

        when(applicationRepository.findByIdAndUser(1L, jack))
            .thenReturn(Optional.of(existing));
        applicationService.delete(1L);

        verify(applicationRepository).delete(existing);
    }

    @Test
    void shouldNotDeleteUnavailableApplication() {
        currentUserExists();
        when(applicationRepository.findByIdAndUser(2L, jack))
            .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> applicationService.delete(2L));

        assertEquals("Application not found", ex.getMessage());
        
        verify(applicationRepository).findByIdAndUser(2L, jack);
        verify(applicationRepository, never()).delete(any(Application.class));
    }
}
