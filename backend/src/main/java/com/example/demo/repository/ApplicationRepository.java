package com.example.demo.repository;
import com.example.demo.entity.Application;
import com.example.demo.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserOrderByDateAppliedDesc(User user);
    Optional<Application>findByIdAndUser(Long id, User user);
}
