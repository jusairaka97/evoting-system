package com.collegeelection.service;

import com.collegeelection.model.Candidate;
import com.collegeelection.model.Election;
import com.collegeelection.model.User;
import com.collegeelection.model.Vote;
import com.collegeelection.repository.CandidateRepository;
import com.collegeelection.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private ElectionService electionService;

    @Override
    public boolean hasUserVoted(User user) {
    	Election election = electionService.getLiveElection();
        return voteRepository.existsByVoterAndElection(user, election);
    }
    @Override
    public boolean castVote(User user, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) return false;
        

        Election election = electionService.getLiveElection();
        if (election == null) return false;

        boolean alreadyVoted = voteRepository.existsByVoterAndElection(user, election);
        if (alreadyVoted) return false;

        Vote vote = new Vote();
        vote.setVoter(user);
        vote.setCandidate(candidate);
        vote.setElection(election);

        voteRepository.save(vote);
        return true;
        
        
        
    }

    @Override
    public List<Object[]> getVotingResults() {
        // Returns candidate name and vote count
        return voteRepository.countVotesByCandidate();
    }
    
    @Override
    public boolean isElectionEnded() {
       
        LocalDateTime electionEndTime = LocalDateTime.of(2025, 5, 1, 12, 00);
        LocalDateTime currentTime = LocalDateTime.now();

        
        return currentTime.isAfter(electionEndTime);
    }
	@Override
	public boolean hasUserVotedForPosition(User user, Election election, String position) {
		return voteRepository.existsByVoterAndElection(user, election);

	}
}
