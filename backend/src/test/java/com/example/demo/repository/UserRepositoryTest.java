package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.example.demo.entity.User;


@DataJpaTest 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers 
public class UserRepositoryTest{
    @Container 
    @ServiceConnection 
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired 
    private UserRepository userRepository;

    @Test 
    void shouldFindUserByEmail(){
        User jack = new User();
        jack.setEmail("jack@example.com");
        jack.setFirstName("Jack");
        jack.setLastName("Sparrow");
        jack.setPassword("encoded-password");
        jack.setBirthDate(LocalDate.of(2000,1,1));

        jack = userRepository.save(jack);

        Optional<User>result = userRepository.findByEmail("jack@example.com");

        assertTrue(result.isPresent());
        assertEquals("Jack", result.get().getFirstName());
        assertEquals("jack@example.com", result.get().getEmail());
    }

    @Test 
    void shouldReturnEmptyWhenEmailDoesNotExist(){
        Optional<User> result = userRepository.findByEmail("unknown@example.com");

        assertTrue(result.isEmpty());
    }

    @Test 
    void shouldCheckIfEmailExists(){
        User jack = new User();
        jack.setEmail("jack@example.com");
        jack.setFirstName("Jack");
        jack.setLastName("Sparrow");
        jack.setPassword("encoded-password");
        jack.setBirthDate(LocalDate.of(2000,1,1));

        jack = userRepository.save(jack);

        assertTrue(userRepository.existsByEmail("jack@example.com"));
        assertFalse(userRepository.existsByEmail("unknown@example.com"));
    }

    @Test 
    void shouldRejectDuplicateEmail(){
        User jack = new User();
        jack.setEmail("jack@example.com");
        jack.setFirstName("Jack");
        jack.setLastName("Sparrow");
        jack.setPassword("encoded-password");
        jack.setBirthDate(LocalDate.of(2000,1,1));

        jack = userRepository.save(jack);

        User john = new User();
        john.setEmail("jack@example.com");
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setPassword("another-encoded-password");
        john.setBirthDate(LocalDate.of(1998,1,1));

       

        assertThrows(DataIntegrityViolationException.class, ()->{
            userRepository.save(john);
            userRepository.flush();
        });


    }




}
