package com.collegeelection.service;

import com.collegeelection.model.Candidate;
import com.collegeelection.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepo;

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        candidateRepo.save(candidate);
    }

    @Override
    public Candidate getCandidateById(Long id) {
        return candidateRepo.findById(id).orElse(null);
    }
    
    @Override
    public void deleteCandidate(Long id) {
    	candidateRepo.deleteById(id);
    }

    @Override
	public Map<String, List<Candidate>> getCandidatesGroupedByPosition() {
		 List<Candidate> allCandidates = candidateRepo.findAll();
		    return allCandidates.stream()
		        .collect(Collectors.groupingBy(Candidate::getPosition));
		}
	
}

