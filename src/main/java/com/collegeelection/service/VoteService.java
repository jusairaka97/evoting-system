package com.collegeelection.service;

import java.util.List;

import com.collegeelection.model.Election;
import com.collegeelection.model.User;

public interface VoteService {
	boolean hasUserVoted(User user);

    boolean castVote(User user, Long candidateId);

    List<Object[]> getVotingResults();

	boolean isElectionEnded();
	boolean hasUserVotedForPosition(User user, Election election, String position);
}
