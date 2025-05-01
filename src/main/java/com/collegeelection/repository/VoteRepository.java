package com.collegeelection.repository;

import com.collegeelection.model.Vote;
import com.collegeelection.model.Candidate;
import com.collegeelection.model.Election;
import com.collegeelection.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository extends JpaRepository<Vote, Long> {
	boolean existsByVoter(User voter);
    
	@Query("SELECT v.candidate.name, COUNT(v) FROM Vote v GROUP BY v.candidate.name")
	List<Object[]> countVotesByCandidate();

    long countByCandidate(Candidate candidate);
    boolean existsByVoterAndElectionAndPosition(User voter, Election election, String position);
    boolean existsByVoterAndElection(User voter, Election election);

	boolean existsByVoterAndPosition(User user, String position);
}
