package com.collegeelection.controller;

import com.collegeelection.model.Candidate;
import com.collegeelection.model.Election;
import com.collegeelection.service.CandidateService;
import com.collegeelection.service.ElectionService;
import com.collegeelection.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private CandidateService candidateService;
    @Autowired private ElectionService electionService;
    @Autowired private VoteService voteService;

    @GetMapping("/admin_dashboard")
    public String dashboard() {
        return "admin/admin_dashboard";
    }
//--------------------------manage candidates----------------------------------------------
    @GetMapping("/manage_candidates")
    public String viewCandidates(Model model) {
        model.addAttribute("candidates", candidateService.getAllCandidates());
        return "admin/manage_candidates";
    }

    @PostMapping("/manage_candidates/add")
    public String addCandidate(@ModelAttribute Candidate candidate) {
        candidateService.saveCandidate(candidate);
        return "redirect:/admin/manage_candidates";
    }

    @GetMapping("/manage_candidates/delete/{id}")
    public String deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return "redirect:/admin/manage_candidates";
    }
  //------------------------------election status------------------------------------------
    @PostMapping("/election_status/toggle")
    public String toggleElectionStatus() {
        electionService.toggleElection();
        return "redirect:/admin/admin_dashboard";
        
    }

    @GetMapping("/election_status")
    public String viewElectionStatus(Model model) {
        Election liveElection = electionService.getLiveElection();
        boolean isActive = electionService.isElectionActive();

        model.addAttribute("isActive", isActive);
        model.addAttribute("liveElection", liveElection);

        return "admin/election_status";
    }

 //-------------------------admin result-------------------------------------------------   
    
    @GetMapping("/admin_results")
    public String showResults(Model model) {
        List<Object[]> results = voteService.getVotingResults();
        model.addAttribute("results", results);
        return "admin/admin_results";
    }
    //-----------------------manage election-------------------------------------------------
    @GetMapping("/create_election")
    public String showCreateElectionForm(Model model) {
        model.addAttribute("election", new Election());
        return "admin/create_election";
    }
    
    @PostMapping("/create_election")
    public String createElection(@ModelAttribute Election election) {
        electionService.saveElection(election);
        return "redirect:/admin/manage_elections";
    }
    @GetMapping("/manage_elections")
    public String manageElections(Model model) {
    	List<Election> elections = electionService.getAllElections();
        Map<Long, String> statusMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (Election e : elections) {
            if (now.isBefore(e.getStartTime())) {
                statusMap.put(e.getId(), "Upcoming");
            } else if (now.isAfter(e.getEndTime())) {
                statusMap.put(e.getId(), "Ended");
            } else {
                statusMap.put(e.getId(), "Live");
            }
        }

        model.addAttribute("elections", elections);
        model.addAttribute("statusMap", statusMap);
        return "admin/manage_elections";
    }
    @PostMapping("/manage_elections/{id}/toggle")
    public String toggleElection(@PathVariable Long id) {
        electionService.toggleElection(id);
        return "redirect:/admin/manage_elections";
    }
    //----------------------------------------------------------------------------
}
