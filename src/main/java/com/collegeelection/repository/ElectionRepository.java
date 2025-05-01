package com.collegeelection.repository;

import com.collegeelection.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    Optional<Election> findTopByOrderByIdDesc(); 
    Optional<Election> findById(Long id); 
    List<Election> findAll(); 
    @Query("SELECT e FROM Election e WHERE e.isLive = true")
    Election findFirstByIsLiveTrue();
}
