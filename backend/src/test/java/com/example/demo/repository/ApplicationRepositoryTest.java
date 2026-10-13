package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.testcontainers.postgresql.PostgreSQLContainer;

import com.example.demo.entity.Application;
import com.example.demo.entity.User;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;



@DataJpaTest 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class ApplicationRepositoryTest {

    @Container 
    @ServiceConnection 
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");


    @Autowired 
    private ApplicationRepository applicationRepository;

    @Autowired 
    private UserRepository userRepository; 

    @Test 
    void shouldSaveApplicationToDatabase(){
        User user = new User();
        user.setEmail("jack@example.com");
        user.setBirthDate(LocalDate.of(2000,07,14));
        user.setFirstName("Jack");
        user.setLastName("Sparrow");
        user.setPassword("Password1234");

        user = userRepository.save(user);

        Application application = new Application();
        application.setCompany("Google");
        application.setRole("Backend Developer");
        application.setStatus(Application.Status.APPLIED);
        application.setDateApplied(LocalDate.now());
        application.setLocation("Vienna");
        application.setUser(user);

        Application savedApplication = applicationRepository.save(application);

        assertNotNull(savedApplication.getId());
        assertEquals("Google", savedApplication.getCompany());
        assertEquals(user.getId(), savedApplication.getUser().getId());
    }

    @Test 
    void shouldFindApplicationsBelongingToUser(){
        User jack = new User();
        jack.setEmail("jack@example.com");
        jack.setFirstName("Jack");
        jack.setLastName("Sparrow");
        jack.setPassword("encoded-password");
        jack.setBirthDate(LocalDate.of(2000,1,1));
        jack= userRepository.save(jack);

        User john = new User();
        john.setEmail("john@example.com");
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setPassword("encoded-password");
        john.setBirthDate(LocalDate.of(2001,1,1));
        john = userRepository.save(john);

        Application amazon = new Application();
        amazon.setCompany("Amazon");
        amazon.setRole("Java Developer");
        amazon.setStatus(Application.Status.APPLIED);
        amazon.setDateApplied(LocalDate.of(2026,10,7));
        amazon.setLocation("Vienna");
        amazon.setUser(jack);
      
        Application google = new Application();
        google.setCompany("Google");
        google.setRole("Software Engineer");
        google.setStatus(Application.Status.APPLIED);
        google.setDateApplied(LocalDate.of(2026, 10, 5));
        google.setLocation("Vienna");
        google.setUser(jack);


        Application microsoft = new Application();
        microsoft.setCompany("Microsoft");
        microsoft.setRole("Backend Developer");
        microsoft.setStatus(Application.Status.APPLIED);
        microsoft.setDateApplied(LocalDate.of(2026, 9, 9));
        microsoft.setLocation("Berlin");
        microsoft.setUser(john);

        applicationRepository.saveAll(List.of(amazon, google, microsoft));

        List<Application> result = applicationRepository.findByUserOrderByDateAppliedDesc(jack);

        assertEquals(2, result.size());
        assertEquals("Amazon", result.get(0).getCompany());
        assertEquals("Google", result.get(1).getCompany());

    }

    @Test
    void shouldNotFindApplicationOfAnotherUser() {
        User jack = new User();
            jack.setEmail("jack@example.com");
            jack.setFirstName("Jack");
            jack.setLastName("Sparrow");
            jack.setPassword("encoded-password");
            jack.setBirthDate(LocalDate.of(2000,1,1));
            jack= userRepository.save(jack);

            User john = new User();
            john.setEmail("john@example.com");
            john.setFirstName("John");
            john.setLastName("Doe");
            john.setPassword("encoded-password");
            john.setBirthDate(LocalDate.of(2001,1,1));
            john = userRepository.save(john);

            Application amazon = new Application();
            amazon.setCompany("Amazon");
            amazon.setRole("Java Developer");
            amazon.setStatus(Application.Status.APPLIED);
            amazon.setDateApplied(LocalDate.of(2026,10,7));
            amazon.setLocation("Vienna");
            amazon.setUser(jack);
            amazon = applicationRepository.save(amazon);

            assertTrue(applicationRepository.findByIdAndUser(amazon.getId(), jack).isPresent());
            assertTrue(applicationRepository.findByIdAndUser(amazon.getId(), john).isEmpty());
        }
    
}
