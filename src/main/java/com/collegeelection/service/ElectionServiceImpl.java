package com.collegeelection.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.collegeelection.model.Election;
import com.collegeelection.repository.ElectionRepository;

@Service
public class ElectionServiceImpl implements ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Override
    public boolean isElectionEnded() {
        return getCurrentElection()
                .map(e -> LocalDateTime.now().isAfter(e.getEndTime()))
                .orElse(true); // Treat no election as ended
    }

    @Override
    public boolean isElectionActive() {
        return getCurrentElection()
                .map(e -> {
                    LocalDateTime now = LocalDateTime.now();
                    return now.isAfter(e.getStartTime()) && now.isBefore(e.getEndTime());
                })
                .orElse(false);
    }

    @Override
    public void startElection(LocalDateTime startTime, LocalDateTime endTime) {
        Election election = new Election();
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        electionRepository.save(election);
    }

    @Override
    public void stopElection() {
        getCurrentElection().ifPresent(election -> {
            election.setEndTime(LocalDateTime.now());
            electionRepository.save(election);
        });
    }

    @Override
    public Optional<Election> getCurrentElection() {
        return electionRepository.findTopByOrderByIdDesc();
       
    }
    
    @Override
    public void toggleElection() {
        electionRepository.findTopByOrderByIdDesc().ifPresent(election -> {
            election.setLive(!election.isLive());
            electionRepository.save(election);
        });
    }

	@Override
	public void saveElection(Election election) {
        electionRepository.save(election);
    }

	@Override
	 public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
	@Override
	public void toggleElection(Long electionId) {
        electionRepository.findById(electionId).ifPresent(election -> {
            election.setLive(!election.isLive());
            electionRepository.save(election);
        });
    }

	@Override
	public Election getLiveElection() {
		 return electionRepository.findFirstByIsLiveTrue(); 
	}

	@Override
	public boolean isElectionLive() {
		Election activeElection = electionRepository.findFirstByIsLiveTrue();
	    if (activeElection == null) return false;

	    LocalDateTime now = LocalDateTime.now();

	    return !now.isBefore(activeElection.getStartTime()) &&
	           !now.isAfter(activeElection.getEndTime());
	}
}
