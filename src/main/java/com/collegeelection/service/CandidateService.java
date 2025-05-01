package com.collegeelection.service;

import com.collegeelection.model.Candidate;
import java.util.List;
import java.util.Map;

public interface CandidateService {
    List<Candidate> getAllCandidates();
    void saveCandidate(Candidate candidate);
    Candidate getCandidateById(Long id);
    void deleteCandidate(Long id);
    public Map<String, List<Candidate>> getCandidatesGroupedByPosition();
}

