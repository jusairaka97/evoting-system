package com.collegeelection.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.collegeelection.model.Election;

public interface ElectionService {
	public boolean isElectionEnded();

	public boolean isElectionActive();

	public void startElection(LocalDateTime startTime, LocalDateTime endTime);

	public void stopElection();

	Optional<Election> getCurrentElection();

	public void toggleElection();

	public void saveElection(Election election);

	public List<Election> getAllElections();

	public void toggleElection(Long electionId);
	public Election getLiveElection();
	public boolean isElectionLive();

}
