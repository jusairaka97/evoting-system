package com.collegeelection.repository;

import com.collegeelection.model.Candidate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Candidate findByName(String name); 
}

